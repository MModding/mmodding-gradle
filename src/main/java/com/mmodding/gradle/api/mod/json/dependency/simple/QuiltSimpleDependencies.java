package com.mmodding.gradle.api.mod.json.dependency.simple;

import com.mmodding.gradle.api.mod.json.dependency.QuiltModDependency;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;

public class QuiltSimpleDependencies extends SimpleDependencies<QuiltModDependency> {

	@Override
	public void addDependency(String namespace, String version) {
		this.dependencies.add(new QuiltModDependency(namespace, version));
	}

	@Override
	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginArray();
		for (QuiltModDependency dependency : this.dependencies) {
			dependency.writeJson(writer);
		}
		writer.endArray();
	}
}
