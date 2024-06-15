package com.mmodding.gradle.api.mod.json.dependency.simple;

import com.mmodding.gradle.api.mod.json.dependency.ModDependency;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class SimpleDependencies<D extends ModDependency> implements Serializable {

	protected final Set<D> dependencies = new HashSet<>();

	public boolean isEmpty() {
		return this.dependencies.isEmpty();
	}

	public void addDependency(String namespace) {
		this.addDependency(namespace, "*");
	}

	public abstract void addDependency(String namespace, String version);

	public abstract void writeJson(JsonWriter writer) throws IOException;
}
