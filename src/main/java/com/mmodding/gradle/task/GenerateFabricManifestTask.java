package com.mmodding.gradle.task;

import com.mmodding.gradle.api.manifest.FabricModManifest;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class GenerateFabricManifestTask extends GenerateManifestTask<FabricModManifest> {
	@Input
	public abstract Property<FabricModManifest> getModManifest();
}
