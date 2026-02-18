package com.mmodding.gradle.impl;

import com.mmodding.gradle.api.MModdingGradle;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Represents the MModding Gradle Plugin.
 *
 * @author FirstMegaGame4, LambdAurora for mod json base
 */
public class MModdingGradlePlugin implements Plugin<Project> {

	private static final String DEBUG_MODE_PROPERTY = "mmodding.gradle.debug";

	/**
	 * Represents whether the debug mode is enabled or not using the {@value #DEBUG_MODE_PROPERTY} system property.
	 */
	public static final boolean DEBUG_MODE = Boolean.getBoolean(MModdingGradlePlugin.DEBUG_MODE_PROPERTY);

	@Override
	public void apply(Project project) {
		project.getExtensions().create(MModdingGradle.class, "mmodding", MModdingGradleImpl.class, project);
		project.getTasks().register(
			"customFMJGeneration",
			CustomModJsonGenerationTask.class,
			task -> {
				task.mustRunAfter("processIncludeJars");
				project.getTasks().getByName("remapJar").dependsOn(task);
				task.getOutputDirectory().set(project.getLayout().getBuildDirectory().dir("processIncludeJars"));
			}
		);
	}
}
