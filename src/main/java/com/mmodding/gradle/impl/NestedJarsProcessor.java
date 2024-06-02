package com.mmodding.gradle.impl;

import com.mmodding.gradle.api.mod.json.ModJson;
import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.task.RemapJarTask;
import net.fabricmc.loom.task.RemapTaskConfiguration;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Provider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an advanced nested JAR processor for Loom's {@code remapJar} task.
 * <p>
 * This works by hijacking the provider in the {@code remapJar} task input by replacing it with a remapped provider.
 * <p>
 * This nested JAR processor only targets JARs that are not originally mods.
 *
 * @author FirstMegaGame4, LambdAurora
 */
@ApiStatus.Internal
public class NestedJarsProcessor {

	private final Map<Metadata, Set<ModJson<?, ?>>> toProcess = new HashMap<>();
	private final Project project;
	private boolean injected = false;

	public NestedJarsProcessor(Project project) {
		this.project = project;
	}

	/**
	 * Adds a manifest to inject into the given dependency.
	 *
	 * @param metadata the dependency identifier
	 * @param manifest the manifest to inject
	 */
	public void addManifest(@NotNull Metadata metadata, @NotNull ModJson<?, ?> manifest) {
		var set = this.toProcess.computeIfAbsent(metadata, md -> new HashSet<>());

		for (var oManifest : set) {
			if (oManifest.getClass().equals(manifest.getClass())) {
				throw new IllegalArgumentException("Cannot add multiple manifests of the same type to the same dependency.");
			}
		}

		// Add some manifest data that is always relevant for non-mod libraries.
		manifest.withCustom(block -> {
			block.withBlock("modmenu", modMenu -> {
				modMenu.withArray("badges", badges -> {
					badges.addUnique("library");
				});
				modMenu.put("update_checker", false);
			});
		});

		set.add(manifest);
	}

	/**
	 * Injects this JAR processor into the {@code remapJar} task input.
	 *
	 * @param project the targeted project
	 */
	public void injectIntoTask(@NotNull Project project) {
		if (this.injected) {
			return;
		}

		this.injected = true;

		this.project.getLogger().lifecycle("Injecting custom nested JARs processor into loom inputs...");

		for (var remapTask : project.getTasksByName(RemapTaskConfiguration.REMAP_JAR_TASK_NAME, false)) {
			if (remapTask instanceof RemapJarTask remapJarTask) {
				remapJarTask.getNestedJars().setFrom(this.remapTaskInput(remapJarTask));
			}
		}
	}

	/**
	 * Remaps the {@code remapJar} task input.
	 *
	 * @param remapJarTask the targeted task
	 * @return a set of remapped inputs
	 */
	private Set<?> remapTaskInput(@NotNull RemapJarTask remapJarTask) {
		// Lookup the existing inputs.
		return remapJarTask.getNestedJars().getFrom().stream()
				.map(from -> {
					// Loom adds a provider for the nest JARs, we wanted to inject a remap step.
					if (from instanceof Provider<?> provider) {
						return provider.map(o -> {
							if (o instanceof ConfigurableFileCollection files) {
								this.remapNestedJars(files.getFrom());
								return files.getFrom();
							} else {
								return o;
							}
						});
					} else {
						return from;
					}
				})
				.collect(Collectors.toSet());
	}

	/**
	 * Propagates the remap step to the task input.
	 *
	 * @param inputs the given input
	 */
	private void remapNestedJars(@NotNull Set<Object> inputs) {
		inputs.forEach(input -> {
			if (input instanceof ConfigurableFileCollection files) {
				// Recurse.
				this.remapNestedJars(files.getFrom());
			} else if (input instanceof File file) {
				this.remapNestedJar(file);
			}
		});
	}

	/**
	 * Attempts to remap the given nested JAR.
	 *
	 * @param file the file of the Loom-transformed JAR
	 */
	@SuppressWarnings("UnstableApiUsage")
	private void remapNestedJar(@NotNull File file) {
		Path path = file.toPath().toAbsolutePath();

		// Attempts to reconstruct the dependency metadata from the file path.
		Path versionPath = path.getParent().getParent();
		String version = versionPath.getFileName().toString();
		Path artifactPath = versionPath.getParent();
		String artifact = artifactPath.getFileName().toString();

		// Note: only non-mod JARs get put in this build cache, which is perfect to differentiate non-mod libraries.
		Path buildCachePath = LoomGradleExtension.get(this.project).getFiles().getProjectBuildCache().toPath().toAbsolutePath();
		Path modProcessingCachePath = buildCachePath.resolve("temp/modprocessing").toAbsolutePath();
		var group = new ArrayList<String>();

		Path groupPath = artifactPath.getParent();
		while (!groupPath.equals(modProcessingCachePath)) {
			group.add(0, groupPath.getFileName().toString());
			groupPath = groupPath.getParent();
		}

		var metadata = new Metadata(
				String.join(".", group),
				artifact,
				version
		);

		var manifests = this.toProcess.get(metadata);

		if (manifests == null || manifests.isEmpty()) {
			return;
		}

		this.project.getLogger().lifecycle("Found dependency {} to process!", metadata);

		try (var zipFs = FileSystems.newFileSystem(path)) {
			for (var manifest : manifests) {
				var manifestPath = zipFs.getPath(manifest.getFileName());
				manifest.writeJson(manifestPath);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public record Metadata(String group, String artifact, String version) {}
}
