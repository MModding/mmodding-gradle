package com.mmodding.gradle.api.mod.json;

import org.gradle.api.Action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class InjectedInterfaces implements Serializable {

	private final Map<String, Set<String>> iifs = new HashMap<>();

	public boolean isEmpty() {
		return this.iifs.isEmpty();
	}

	public void injectTo(String target, String iif) {
		String patched = target.replace(".", "/").replace("$", "\\u0024");
		this.iifs.putIfAbsent(patched, new HashSet<>());
		this.iifs.get(patched).add(iif.replace(".", "/").replace("$", "\\u0024"));
	}

	public void injectTo(String target, Set<String> iifs) {
		this.iifs.put(
			target.replace(".", "/").replace("$", "\\u0024"),
			iifs.stream().map(iif -> iif.replace(".", "/").replace("$", "\\u0024")).collect(Collectors.toSet())
		);
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
