package com.mmodding.gradle.api;

import com.mmodding.gradle.api.architecture.Modules;
import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.QuiltModJson;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

/**
 * Represents MModding Gradle to configure parts of it in buildscripts.
 *
 * @author FirstMegaGame4, and formerly LambdAurora for mod.json handling base.
 */
public interface MModdingGradle {

	/**
	 * Configures and generates a <code>fabric.mod.json</code>.
	 * @param action the FMJ configuration
	 */
	void configureFabricModJson(Action<FabricModJson> action);

	/**
	 * Configures and generates a <code>quilt.mod.json</code>.
	 * @param action the QMJ configuration
	 */
	void configureQuiltModJson(Action<QuiltModJson> action);

	/**
	 * Configures the <code>fabric.mod.json</code> of a dependency.
	 * @param dependency the dependency
	 * @param action the FMJ configuration
	 * @return the dependency
	 */
	Dependency configureFMJForDependency(Dependency dependency, Action<FabricModJson> action);

	/**
	 * Configures the <code>quilt.mod.json</code> of a dependency.
	 * @param dependency the dependency
	 * @param action the QMJ configuration
	 * @return the dependency
	 */
	Dependency configureQMJForDependency(Dependency dependency, Action<QuiltModJson> action);

	/**
	 * Manages modules inside a multi-project.
	 * @param current the current project to manage
	 * @param action the modules management
	 * @return the current project
	 */
	Project modules(Project current, Action<Modules> action);
}
