package com.mmodding.gradle.task;

import com.mmodding.gradle.api.mod.json.ModJson;
import com.mmodding.gradle.api.mod.json.dependency.ModDependencies;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class GenerateModJson<R extends ModDependencies.ModDependency, D extends ModDependencies<R>, M extends ModJson<R, D>> extends DefaultTask {

	@Input
	public abstract Property<M> getModJson();

	@OutputDirectory
	public abstract DirectoryProperty getOutputDir();

	@Inject
	public GenerateModJson() {
		this.setGroup("generation");
		this.getOutputDir().convention(
			this.getProject().getLayout().getBuildDirectory().dir("generated/generated_resources")
		);
	}

	@TaskAction
	public void generate() throws IOException {
		var modJson = this.getModJson().get();
		Path output = this.getOutputDir().getAsFile().get().toPath().resolve(modJson.getFileName());

		if (Files.exists(output)) {
			Files.delete(output);
		}

		modJson.writeJson(output);
	}
}
