package com.mmodding.gradle.impl.task;

import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.dependency.FabricModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.FabricAdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.FabricSimpleDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

import javax.inject.Inject;

public abstract class GenerateFabricModJson extends GenerateModJson<FabricModDependency, FabricAdvancedDependencies, FabricSimpleDependencies, FabricModJson> {

	@Inject
	public GenerateFabricModJson(boolean isTestmod) {
		super(isTestmod);
	}

	@Input
	public abstract Property<FabricModJson> getModJson();
}
