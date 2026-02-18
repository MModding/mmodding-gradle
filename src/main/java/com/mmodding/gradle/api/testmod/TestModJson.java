package com.mmodding.gradle.api.testmod;

import com.mmodding.gradle.api.EnvironmentTarget;
import com.mmodding.gradle.api.mod.json.ModEntrypoints;
import com.mmodding.gradle.api.mod.json.dependency.advanced.FabricAdvancedDependencies;
import org.gradle.api.Action;

import java.io.Serializable;


public interface TestModJson extends Serializable {

	String getNamespace();

	void setNamespace(String namespace);

	String getName();

	void setName(String name);

	String getLicense();

	void setLicense(String license);

	EnvironmentTarget getEnvironment();

	void setEnvironment(EnvironmentTarget environment);

	ModEntrypoints getEntrypoints();

	void withEntrypoints(Action<ModEntrypoints> action);

	FabricAdvancedDependencies getDependencies();

	void withDependencies(Action<FabricAdvancedDependencies> action);
}
