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

	public void init(Class<?>... entrypoints) {
		this.custom(this.isQuilt ? "init" : "main", entrypoints);
	}

	public void client(Class<?>... entrypoints) {
		this.custom(this.isQuilt ? "client_init" : "client", entrypoints);
	}

	public void server(Class<?>... entrypoints) {
		this.custom(this.isQuilt ? "server_init" : "server", entrypoints);
	}

	public void init(String... entrypoints) {
		this.custom(this.isQuilt ? "init" : "main", entrypoints);
	}

	public void client(String... entrypoints) {
		this.custom(this.isQuilt ? "client_init" : "client", entrypoints);
	}

	public void server(String... entrypoints) {
		this.custom(this.isQuilt ? "server_init" : "server", entrypoints);
	}

	public void custom(String key, Class<?>... entrypoints) {
		this.custom(
			key, Arrays.stream(entrypoints).map(entrypoint -> entrypoint.getName().replace(".", "/").replace("$", "\\u0024"))
				.toArray(String[]::new)
		);
	}

	public void custom(String key, String... entrypoints) {
		this.entrypoints.put(key, Arrays.stream(entrypoints).collect(Collectors.toSet()));
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
