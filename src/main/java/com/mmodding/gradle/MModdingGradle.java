/*
 * Copyright 2023 Yumi Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmodding.gradle;

import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.impl.NestedJarsProcessor;
import com.mmodding.gradle.task.GenerateFabricModJson;
import com.mmodding.gradle.util.LoomProvider;
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
	private final NestedJarsProcessor nestedJarsProcessor;

	@Inject
	public MModdingGradle(final ObjectFactory objects, final Project project) {
		this.project = project;
		this.loomProvider = new LoomProvider(project);
		this.nestedJarsProcessor = new NestedJarsProcessor(project);
	}

	public void configureFabricModJson(Action<FabricModJson> action) {
		var fmj = new FabricModJson();
		fmj.fillDefaults(this.project);
		action.execute(fmj);

		GenerateFabricModJson task = this.project.getTasks().create("genFabricModManifest", GenerateFabricModJson.class);
		task.getModJson().set(fmj);
		this.project.getTasks().getByPath("ideaSyncTask").dependsOn(task);

		JavaPluginExtension javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
		SourceSet mainSourceSet = javaExtension.getSourceSets().getByName("main");
		mainSourceSet.getResources().srcDir(task);
	}

	public Dependency configureFMJForDependency(Dependency dependency, Action<FabricModJson> action) {
		this.nestedJarsProcessor.injectIntoTask(this.project);

		FabricModJson modJson = new FabricModJson();
		modJson.setName(dependency.getName());
		modJson.setVersion(dependency.getVersion());

		action.execute(modJson);

		this.nestedJarsProcessor.addManifest(
			new NestedJarsProcessor.Metadata(dependency.getGroup(), dependency.getName(), dependency.getVersion()),
			modJson
		);

		return dependency;
	}
}