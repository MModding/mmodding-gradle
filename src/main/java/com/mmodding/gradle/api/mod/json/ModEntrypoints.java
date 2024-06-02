package com.mmodding.gradle.api.mod.json;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ModEntrypoints implements Serializable {

	private final boolean isQuilt;

	private final Map<String, Set<String>> entrypoints = new HashMap<>();

	public ModEntrypoints(boolean isQuilt) {
		this.isQuilt = isQuilt;
	}

	public boolean isEmpty() {
		return this.entrypoints.isEmpty();
	}

	public void init(String entrypoint) {
		this.custom(this.isQuilt ? "init" : "main", entrypoint);
	}

	public void init(Set<String> entrypoints) {
		this.custom(this.isQuilt ? "init" : "main", entrypoints);
	}

	public void client(String entrypoint) {
		this.custom(this.isQuilt ? "client_init" : "client", entrypoint);
	}

	public void client(Set<String> entrypoints) {
		this.custom(this.isQuilt ? "client_init" : "client", entrypoints);
	}

	public void server(String entrypoint) {
		this.custom(this.isQuilt ? "server_init" : "server", entrypoint);
	}

	public void server(Set<String> entrypoints) {
		this.custom(this.isQuilt ? "server_init" : "server", entrypoints);
	}

	public void custom(String key, String entrypoint) {
		this.entrypoints.putIfAbsent(key, new HashSet<>());
		this.entrypoints.get(key).add(entrypoint);
	}

	public void custom(String key, Set<String> entrypoints) {
		this.entrypoints.put(key, entrypoints.stream().map(entrypoint -> entrypoint.replace("/", ".").replace("$", "\\u0024")).collect(Collectors.toSet()));
	}

	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginObject();
		for (Map.Entry<String, Set<String>> entry : this.entrypoints.entrySet()) {
			writer.name(entry.getKey()).beginArray();
			for (String entrypoint : entry.getValue()) {
				writer.value(entrypoint);
			}
			writer.endArray();
		}
		writer.endObject();
	}
}
