package com.mmodding.gradle.api;

import com.mmodding.gradle.api.architecture.Modules;
import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.QuiltModJson;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;

import java.util.function.Predicate;

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
	 * Configures the Test Mod for the current module. Applies to the <code>testmod</code> source set.
	 */
	void configureTestmod();

	/**
	 * Registers every project of the multi-project structure as a mod through the Loom DSL.
	 * <br>
	 * For single-project structures, this only registers the actual project.
	 */
	void loomModRegistration();

	/**
	 * Registers some projects of the multi-project structure as a mod through the Loom DSL.
	 * @param filter the project filter
	 */
	void loomModRegistration(Predicate<Project> filter);

	/**
	 * Registers every project test mod of the multi-project structure as a test mod through the Loom DSL.
	 * <br>
	 * For single-project structures, this only registers the actual project.
	 */
	void loomTestmodRegistration();

	/**
	 * Registers some project test mods of the multi-project structure as a test mod through the Loom DSL.
	 * @param filter the project filter
	 */
	void loomTestmodRegistration(Predicate<Project> filter);

	/**
	 * Manages modules inside a multi-project.
	 * @param action the modules management
	 */
	void modules(Action<Modules> action);

	/**
	 * Collects and provides subproject main classpaths to the main project.
	 * <br>
	 * This notably lets the main project Loom to properly detect the presence of subproject generated mod jsons.
	 */
	void collectSubprojectClasspaths();
}
