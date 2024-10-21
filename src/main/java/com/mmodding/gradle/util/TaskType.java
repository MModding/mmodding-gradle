package com.mmodding.gradle.util;

import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.ModJson;
import com.mmodding.gradle.api.mod.json.QuiltModJson;
import com.mmodding.gradle.api.mod.json.dependency.FabricModDependency;
import com.mmodding.gradle.api.mod.json.dependency.ModDependency;
import com.mmodding.gradle.api.mod.json.dependency.QuiltModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.AdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.advanced.FabricAdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.advanced.QuiltAdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.FabricSimpleDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.QuiltSimpleDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.SimpleDependencies;
import com.mmodding.gradle.task.GenerateFabricModJson;
import com.mmodding.gradle.task.GenerateModJson;
import com.mmodding.gradle.task.GenerateQuiltModJson;

import java.util.function.Supplier;

public class TaskType<D extends ModDependency, A extends AdvancedDependencies<D>, S extends SimpleDependencies<D>, M extends ModJson<D, A, S>> {

	public static final TaskType<FabricModDependency, FabricAdvancedDependencies, FabricSimpleDependencies, FabricModJson> FMJ = new TaskType<>("generateFMJ", GenerateFabricModJson.class, "customFMJGeneration", FabricModJson::new);
	public static final TaskType<QuiltModDependency, QuiltAdvancedDependencies, QuiltSimpleDependencies, QuiltModJson> QMJ = new TaskType<>("generateQMJ", GenerateQuiltModJson .class,"customQMJGeneration", QuiltModJson::new);

	private final String taskName;
	private final Class<? extends GenerateModJson<D, A, S, M>> taskClass;
	private final String dependencyName;
	private final Supplier<M> modJsonFactory;

	private TaskType(String taskName, Class<? extends GenerateModJson<D, A, S, M>> taskClass, String dependencyName, Supplier<M> modJsonFactory) {
		this.taskName = taskName;
		this.taskClass = taskClass;
		this.dependencyName = dependencyName;
		this.modJsonFactory = modJsonFactory;
	}

	public String getTaskName() {
		return this.taskName;
	}

	public Class<? extends GenerateModJson<D, A, S, M>> getTaskClass() {
		return this.taskClass;
	}

	public String getDependencyName() {
		return this.dependencyName;
	}

	public M createModJson() {
		return this.modJsonFactory.get();
	}
}
