package com.mmodding.gradle.impl;

import com.mmodding.gradle.api.mod.json.ModJson;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents an advanced Task that injects custom mod json before Loom's {@code remapJar} task.
 * <p>
 * This Task only targets JARs that are not originally mods.
 * </p>
 * @author FirstMegaGame4, LambdAurora for the original concept
 */
public abstract class CustomFMJGenerationTask extends DefaultTask {

	private final Map<Metadata, Set<ModJson<?, ?, ?>>> toProcess = new HashMap<>();

	@OutputDirectory
	public abstract DirectoryProperty getOutputDirectory();

	public void addModJson(@NotNull Metadata metadata, @NotNull ModJson<?, ?, ?> modJson) {
		Set<ModJson<?, ?, ?>> set = this.toProcess.computeIfAbsent(metadata, md -> new HashSet<>());

		for (var oModJson : set) {
			if (oModJson.getClass().equals(modJson.getClass())) {
				throw new IllegalArgumentException("Cannot add multiple mod json of the same type to the same dependency.");
			}
		}

		// Add some mod json data that is always relevant for non-mod libraries.
		modJson.withCustom(block ->
			block.withBlock("modmenu", modMenu -> {
				modMenu.withArray("badges", badges ->
					badges.addUnique("library"));
				modMenu.put("update_checker", false);
			})
		);

		set.add(modJson);
	}

	@TaskAction
	void insertCustomFMJs() {
		for (Map.Entry<Metadata, Set<ModJson<?, ?, ?>>> entry : this.toProcess.entrySet()) {
			Metadata metadata = entry.getKey();
			Provider<RegularFile> provider = this.getOutputDirectory().file(metadata.artifact() + "-" + metadata.version() + ".jar");
			if (provider.isPresent()) {
				File file = provider.get().getAsFile();
				if (file.exists()) {
					this.getProject().getLogger().lifecycle("Found dependency {} to process!", metadata);
					try (var zipFs = FileSystems.newFileSystem(file.toPath())) {
						for (var modJson : entry.getValue()) {
							var modJsonPath = zipFs.getPath(modJson.getFileName());
							modJson.writeJson(modJsonPath);
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				else {
					throw new RuntimeException("Did not find dependency " + metadata + "!");
				}
			}
		}
	}

	public record Metadata(String group, String artifact, String version) {}
}
