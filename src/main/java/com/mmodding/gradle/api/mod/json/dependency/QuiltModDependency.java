package com.mmodding.gradle.api.mod.json.dependency;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;

public class QuiltModDependency extends ModDependency {

	public QuiltModDependency(String namespace, String version) {
		super(namespace, version);
	}

	@Override
	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("id").value(this.namespace);
		writer.name("versions").value(this.version);
		writer.endObject();
	}
}
