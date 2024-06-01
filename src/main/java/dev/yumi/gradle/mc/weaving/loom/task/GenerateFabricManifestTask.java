package dev.yumi.gradle.mc.weaving.loom.task;

import dev.yumi.gradle.mc.weaving.loom.api.manifest.FabricModManifest;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class GenerateFabricManifestTask extends GenerateManifestTask<FabricModManifest> {
	@Input
	public abstract Property<FabricModManifest> getModManifest();
}
