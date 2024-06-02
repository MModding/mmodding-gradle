package com.mmodding.gradle.api.mod.json;

import org.gradle.api.Action;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class InjectedInterfaces implements Serializable {

	private final Map<String, Set<String>> iifs = new HashMap<>();

	public boolean isEmpty() {
		return this.iifs.isEmpty();
	}

	public void apply(Class<?> target, Class<?>... iifs) {
		this.apply(
			target.getName().replace(".", "/").replace("$", "\\u0024"),
			Arrays.stream(iifs).map(iif -> iif.getName().replace(".", "/").replace("$", "\\u0024")).toArray(String[]::new)
		);
	}

	public void apply(String target, String... iifs) {
		this.iifs.put(target, Arrays.stream(iifs).collect(Collectors.toSet()));
	}

	public void fill(CustomElement.CustomBlock custom, boolean isQuilt) {
		Action<CustomElement.CustomBlock> process = block -> {
			for (Map.Entry<String, Set<String>> entry : this.iifs.entrySet()) {
				block.putArray(entry.getKey(), array -> {
					for (String iif : entry.getValue()) {
						array.add(iif);
					}
				});
			}
		};
		if (isQuilt) {
			custom.putBlock("quilt_loom", loom -> loom.putBlock("injected_interfaces", process));
		}
		else {
			custom.putBlock("loom:injected_interfaces", process);
		}
	}
}
