package com.mmodding.gradle.api.architecture;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

import java.util.*;

public class Modules {

	private final Project project;

	public Modules(Project project) {
		this.project = project;
	}

	private void handle(String module) {
		SourceSet mainSourceSet = Objects.requireNonNull(this.project.getExtensions().findByType(JavaPluginExtension.class)).getSourceSets().getByName("main");
		Project dependencyProject = this.project.getRootProject().findProject(":" + module);
		if (dependencyProject == null) {
			throw new IllegalStateException("Dependency project should exist.");
		}
		SourceSet dependencyMainSourceSet = Objects.requireNonNull(dependencyProject.getExtensions().findByType(JavaPluginExtension.class)).getSourceSets().getByName("main");
		mainSourceSet.setCompileClasspath(mainSourceSet.getCompileClasspath().plus(dependencyMainSourceSet.getCompileClasspath()));
		mainSourceSet.setRuntimeClasspath(mainSourceSet.getRuntimeClasspath().plus(dependencyMainSourceSet.getRuntimeClasspath()));
	}

	public void api(String module) {
		Dependency subProject = this.project.getDependencies().project(Map.of("path", ":" + module, "configuration", "namedElements"));
		this.project.getDependencies().add("api", subProject);
		this.handle(module);
	}

	public void implementation(String module) {
		Dependency subProject = this.project.getDependencies().project(Map.of("path", ":" + module, "configuration", "namedElements"));
		this.project.getDependencies().add("implementation", subProject);
		this.handle(module);
	}

	public void include(String module) {
		Dependency subProject = this.project.getDependencies().project(Map.of("path", ":" + module));
		this.project.getDependencies().add("include", subProject);
	}
}
