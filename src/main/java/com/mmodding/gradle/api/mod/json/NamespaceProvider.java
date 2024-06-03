package com.mmodding.gradle.api.mod.json;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class NamespaceProvider implements Serializable {

	private final Set<String> namespaces = new HashSet<>();

	public boolean isEmpty() {
		return this.namespaces.isEmpty();
	}

	public void provide(String namespace) {
		this.namespaces.add(namespace);
	}

	public void provide(Set<String> namespaces) {
		this.namespaces.addAll(namespaces);
	}

	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginArray();
		for (String namespace : this.namespaces) {
			writer.value(namespace);
		}
		writer.endArray();
	}
}
