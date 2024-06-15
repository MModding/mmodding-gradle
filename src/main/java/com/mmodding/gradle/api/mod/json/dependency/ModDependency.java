package com.mmodding.gradle.api.mod.json.dependency;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;

public abstract class ModDependency implements Serializable {

	protected final String namespace;
	protected final String version;

	public ModDependency(String namespace, String version) {
		this.namespace = namespace;
		this.version = version;
	}

	public abstract void writeJson(JsonWriter writer) throws IOException;
}
