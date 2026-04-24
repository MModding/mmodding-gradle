package com.mmodding.gradle.api.architecture;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import java.util.*;

public class Modules {

	private final Project project;

	public Modules(Project project) {
		this.project = project;
	}

	private void handle(String module) {
		Project dependencyProject = this.project.getRootProject().findProject(":" + module);
		if (dependencyProject == null) {
			throw new IllegalStateException("Dependency project should exist.");
		}
	}

	public void api(String module) {
		Dependency subProject = this.project.getDependencies().project(Map.of("path", ":" + module));
		this.project.getDependencies().add("api", subProject);
		this.handle(module);
	}

	public void implementation(String module) {
		Dependency subProject = this.project.getDependencies().project(Map.of("path", ":" + module));
		this.project.getDependencies().add("implementation", subProject);
		this.handle(module);
	}

	public void include(String module) {
		Dependency subProject = this.project.getDependencies().project(Map.of("path", ":" + module));
		this.project.getDependencies().add("include", subProject);
		this.project.getDependencies().add("api", subProject); // Exposed to everyone.
	}

	public void internal(String module) {
		Dependency subProject = this.project.getDependencies().project(Map.of("path", ":" + module));
		this.project.getDependencies().add("include", subProject);
		this.project.getDependencies().add("implementation", subProject); // Hidden from everyone.
	}
}
