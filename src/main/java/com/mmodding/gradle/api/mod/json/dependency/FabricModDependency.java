package com.mmodding.gradle.api.mod.json.dependency;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;

public class FabricModDependency extends ModDependency {

	public FabricModDependency(String namespace, String version) {
		super(namespace, version);
	}

	@Override
	public void writeJson(JsonWriter writer) throws IOException {
		writer.name(this.namespace).value(this.version);
	}
}
