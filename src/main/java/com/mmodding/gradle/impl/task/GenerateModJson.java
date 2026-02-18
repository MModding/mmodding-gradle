package com.mmodding.gradle.impl.task;

import com.mmodding.gradle.api.mod.json.ModJson;
import com.mmodding.gradle.api.mod.json.dependency.ModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.AdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.SimpleDependencies;
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

public abstract class GenerateModJson<D extends ModDependency, A extends AdvancedDependencies<D>, S extends SimpleDependencies<D>, M extends ModJson<D, A, S>> extends DefaultTask {

	@Input
	public abstract Property<M> getModJson();

	@OutputDirectory
	public abstract DirectoryProperty getOutputDir();

	@Inject
	public GenerateModJson(boolean isTestmod) {
		this.setGroup("generation");
		this.getOutputDir().convention(
			this.getProject().getLayout().getBuildDirectory().dir("generated/generated_" + (isTestmod ? "testmod_" : "") + "resources")
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
