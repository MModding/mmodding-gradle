package com.mmodding.gradle.util;

import net.fabricmc.loom.api.LoomGradleExtensionAPI;
import org.gradle.api.Project;

public class LoomProvider {

	private final Project project;
	private LoomGradleExtensionAPI loomExt;

	public LoomProvider(Project project) {
		this.project = project;
	}

	public LoomGradleExtensionAPI getLoom() {
		if (this.loomExt != null) {
			return this.loomExt;
		}

		this.loomExt = this.project.getExtensions().getByType(LoomGradleExtensionAPI.class);
		return this.loomExt;
	}
}
