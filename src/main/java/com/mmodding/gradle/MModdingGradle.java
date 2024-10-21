/*
 * Copyright 2023 Yumi Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmodding.gradle;

import com.mmodding.gradle.api.architecture.Modules;
import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.ModJson;
import com.mmodding.gradle.api.mod.json.QuiltModJson;
import com.mmodding.gradle.api.mod.json.dependency.ModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.AdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.SimpleDependencies;
import com.mmodding.gradle.impl.CustomModJsonGenerationTask;
import com.mmodding.gradle.task.GenerateModJson;
import com.mmodding.gradle.util.LoomProvider;
import com.mmodding.gradle.util.TaskType;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

import javax.inject.Inject;

/**
 * Represents MModding Gradle to configure parts of it in buildscripts.
 *
 * @author FirstMegaGame4, LambdAurora for mod json base
 */
public class MModdingGradle {

	private final Project project;
	private final LoomProvider loomProvider;

	@Inject
	public MModdingGradle(final ObjectFactory objects, final Project project) {
		this.project = project;
		this.loomProvider = new LoomProvider(project);
	}

	private <D extends ModDependency, A extends AdvancedDependencies<D>, S extends SimpleDependencies<D>, M extends ModJson<D, A, S>> void configureModJson(TaskType<D, A, S, M> taskType, Action<M> action) {
		if (this.project.getTasks().findByPath(taskType.getTaskName()) == null) {
			M modJson = taskType.createModJson();
			modJson.fillDefaults(this.project);
			action.execute(modJson);

			GenerateModJson<D, A, S, M> task = this.project.getTasks().create(taskType.getTaskName(), taskType.getTaskClass());
			task.getModJson().set(modJson);
			this.project.getTasks().getByPath("ideaSyncTask").dependsOn(task);

			JavaPluginExtension javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
			SourceSet mainSourceSet = javaExtension.getSourceSets().getByName("main");
			mainSourceSet.getResources().srcDir(task);
		}
		else {
			// Behaving differently if the FabricModJson already exists
			@SuppressWarnings("unchecked")
			GenerateModJson<D, A, S, M> task = (GenerateModJson<D, A, S, M>) this.project.getTasks().getByName(taskType.getTaskName());
			M modJson = task.getModJson().get();
			action.execute(modJson);
			task.getModJson().set(modJson);
		}
	}

	private <D extends ModDependency, A extends AdvancedDependencies<D>, S extends SimpleDependencies<D>, M extends ModJson<D, A, S>> Dependency configureModJsonForDependency(Dependency dependency, TaskType<D, A, S, M> taskType, Action<M> action) {
		CustomModJsonGenerationTask task = (CustomModJsonGenerationTask) this.project.getTasks().findByPath(taskType.getDependencyName());
		assert task != null;

		M modJson = taskType.createModJson();
		modJson.setName(dependency.getName());
		modJson.setVersion(dependency.getVersion());

		action.execute(modJson);
		task.addModJson(new CustomModJsonGenerationTask.Metadata(dependency.getGroup(), dependency.getName(), dependency.getVersion()), modJson);

		return dependency;
	}

	public void configureFabricModJson(Action<FabricModJson> action) {
		this.configureModJson(TaskType.FMJ, action);
	}

	public void configureQuiltModJson(Action<QuiltModJson> action) {
		this.configureModJson(TaskType.QMJ, action);
	}

	public Dependency configureFMJForDependency(Dependency dependency, Action<FabricModJson> action) {
		return this.configureModJsonForDependency(dependency, TaskType.FMJ, action);
	}

	public Dependency configureQMJForDependency(Dependency dependency, Action<QuiltModJson> action) {
		return this.configureModJsonForDependency(dependency, TaskType.QMJ, action);
	}

	public Project modules(Project current, Action<Modules> action) {
		Modules modules = new Modules(false);
		action.execute(modules);
		modules.apply(current.getDependencies());
		return current;
	}
}
