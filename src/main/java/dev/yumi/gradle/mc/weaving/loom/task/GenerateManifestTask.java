package dev.yumi.gradle.mc.weaving.loom.task;

import dev.yumi.gradle.mc.weaving.loom.YumiWeavingLoomGradlePlugin;
import dev.yumi.gradle.mc.weaving.loom.api.manifest.ModManifest;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class GenerateManifestTask<M extends ModManifest> extends DefaultTask {
	@Input
	public abstract Property<M> getModManifest();

	@OutputDirectory
	public abstract DirectoryProperty getOutputDir();

	@Inject
	public GenerateManifestTask() {
		this.setGroup("generation");
		this.getOutputDir().convention(
				this.getProject().getLayout().getBuildDirectory().dir("generated/generated_resources")
		);
	}

	@TaskAction
	public void generate() throws IOException {
		var manifest = this.getModManifest().get();
		Path output = this.getOutputDir().getAsFile().get().toPath().resolve(manifest.getFileName());

		if (Files.exists(output)) {
			Files.delete(output);
		}

		var json = manifest.toJson();
		Files.writeString(output, YumiWeavingLoomGradlePlugin.GSON.toJson(json));
	}
}
