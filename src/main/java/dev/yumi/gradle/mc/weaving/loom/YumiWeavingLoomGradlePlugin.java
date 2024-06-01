/*
 * Copyright 2024 Yumi Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package dev.yumi.gradle.mc.weaving.loom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Represents the Yumi Minecraft Weaving Loom Gradle plugin.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public class YumiWeavingLoomGradlePlugin implements Plugin<Project> {
	private static final String DEBUG_MODE_PROPERTY = "yumi.gradle.mc.weaving_loom.debug";
	/**
	 * Represents whether the debug mode is enabled or not using the {@value #DEBUG_MODE_PROPERTY} system property.
	 */
	public static final boolean DEBUG_MODE = Boolean.getBoolean(DEBUG_MODE_PROPERTY);

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	@Override
	public void apply(Project project) {
		var ext = project.getExtensions().create("weavingLoom", YumiWeavingLoomGradleExtension.class, project);
	}
}
