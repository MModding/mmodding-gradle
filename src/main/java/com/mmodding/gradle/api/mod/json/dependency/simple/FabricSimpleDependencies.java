package com.mmodding.gradle.api.mod.json.dependency.simple;

import com.mmodding.gradle.api.mod.json.dependency.FabricModDependency;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;

public class FabricSimpleDependencies extends SimpleDependencies<FabricModDependency> {

	@Override
	public void addDependency(String namespace, String version) {
		this.dependencies.add(new FabricModDependency(namespace, version));
	}

	@Override
	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginObject();
		for (FabricModDependency dependency : this.dependencies) {
			dependency.writeJson(writer);
		}
		writer.endObject();
	}
}
