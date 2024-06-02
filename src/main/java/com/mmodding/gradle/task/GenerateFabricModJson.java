package com.mmodding.gradle.task;

import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.dependency.FabricModDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class GenerateFabricModJson extends GenerateModJson<FabricModDependencies.FabricModDependency, FabricModDependencies, FabricModJson> {

	@Input
	public abstract Property<FabricModJson> getModJson();
}
