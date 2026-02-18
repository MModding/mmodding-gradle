package com.mmodding.gradle.impl.task;

import com.mmodding.gradle.api.mod.json.QuiltModJson;
import com.mmodding.gradle.api.mod.json.dependency.QuiltModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.QuiltAdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.QuiltSimpleDependencies;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;

public abstract class GenerateQuiltModJson extends GenerateModJson<QuiltModDependency, QuiltAdvancedDependencies, QuiltSimpleDependencies, QuiltModJson> {

	@Input
	public abstract Property<QuiltModJson> getModJson();
}
