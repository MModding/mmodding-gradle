/*
 * Copyright 2024 Yumi Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmodding.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Represents the Yumi Minecraft Weaving Loom Gradle plugin.
 *
 * @author FirstMegaGame4, LambdAurora for manifests
 */
public class MModdingGradlePlugin implements Plugin<Project> {

	private static final String DEBUG_MODE_PROPERTY = "mmodding.gradle.debug";

	/**
	 * Represents whether the debug mode is enabled or not using the {@value #DEBUG_MODE_PROPERTY} system property.
	 */
	public static final boolean DEBUG_MODE = Boolean.getBoolean(MModdingGradlePlugin.DEBUG_MODE_PROPERTY);

	@Override
	public void apply(Project project) {
		project.getExtensions().create("modding", MModdingGradle.class, project);
	}
}
