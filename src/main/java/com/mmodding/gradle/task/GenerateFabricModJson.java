package com.mmodding.gradle.task;

import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.dependency.FabricModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.FabricAdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.FabricSimpleDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class GenerateFabricModJson extends GenerateModJson<FabricModDependency, FabricAdvancedDependencies, FabricSimpleDependencies, FabricModJson> {

	@Input
	public abstract Property<FabricModJson> getModJson();
}
