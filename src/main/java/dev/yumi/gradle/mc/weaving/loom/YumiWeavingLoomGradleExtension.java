/*
 * Copyright 2023 Yumi Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.yumi.gradle.mc.weaving.loom;

import dev.yumi.gradle.mc.weaving.loom.api.manifest.FabricModManifest;
import dev.yumi.gradle.mc.weaving.loom.impl.NestedJarsProcessor;
import dev.yumi.gradle.mc.weaving.loom.task.GenerateFabricManifestTask;
import dev.yumi.gradle.mc.weaving.loom.util.LoomProvider;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;

import javax.inject.Inject;

/**
 * Represents the Yumi Minecraft Weaving Loom Gradle extension to configure the plugin in buildscripts.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class YumiWeavingLoomGradleExtension {
	private final Project project;
	private final LoomProvider loomProvider;
	private final NestedJarsProcessor nestedJarsProcessor;

	@Inject
	public YumiWeavingLoomGradleExtension(final ObjectFactory objects, final Project project) {
		this.project = project;
		this.loomProvider = new LoomProvider(project);
		this.nestedJarsProcessor = new NestedJarsProcessor(project);
	}

	public void addFabricModManifest(Action<FabricModManifest> action) {
		var fmj = new FabricModManifest();
		fmj.fillDefaults(this.project);
		action.execute(fmj);

		var task = this.project.getTasks().create("genFabricModManifest", GenerateFabricManifestTask.class);
		task.getModManifest().set(fmj);
		this.project.getTasks().getByPath("ideaSyncTask").dependsOn(task);

		final JavaPluginExtension javaExtension = project.getExtensions().getByType(JavaPluginExtension.class);
		final SourceSet mainSourceSet = javaExtension.getSourceSets().getByName("main");
		mainSourceSet.getResources().srcDir(task);
	}

	public Dependency withFabricManifest(Dependency dependency, Action<FabricModManifest> action) {
		this.nestedJarsProcessor.injectIntoTask(this.project);

		var manifest = new FabricModManifest();
		manifest.setName(dependency.getName());
		manifest.setVersion(dependency.getVersion());

		action.execute(manifest);

		this.nestedJarsProcessor.addManifest(
				new NestedJarsProcessor.Metadata(dependency.getGroup(), dependency.getName(), dependency.getVersion()),
				manifest
		);

		return dependency;
	}
}
