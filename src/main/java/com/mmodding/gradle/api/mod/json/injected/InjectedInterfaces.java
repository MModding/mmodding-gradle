package com.mmodding.gradle.api.mod.json.injected;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InjectedInterfaces implements Serializable {

	private final Map<String, Set<String>> iifs = new LinkedHashMap<>();

	public boolean isEmpty() {
		return this.iifs.isEmpty();
	}

	public void apply(Class<?> target, Class<?> iif) {
		this.apply(target.getName().replace(".", "/"), iif.getName().replace(".", "/"));
	}

	public void apply(Class<?> target, Set<Class<?>> iifs) {
		this.apply(
			target.getName().replace(".", "/"),
			iifs.stream().map(iif -> iif.getName().replace(".", "/")).collect(Collectors.toSet())
		);
	}

	public void apply(String target, String iif) {
		this.iifs.computeIfAbsent(target, func -> new HashSet<>());
		this.iifs.get(target).add(iif);
	}

	public void apply(String target, Set<String> iifs) {
		this.iifs.put(target, iifs);
	}

	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginObject();
		for (Map.Entry<String, Set<String>> entry : this.iifs.entrySet()) {
			writer.name(entry.getKey()).beginArray();
			for (String iif : entry.getValue()) {
				writer.value(iif);
			}
			writer.endArray();
		}
		writer.endObject();
	}
}
