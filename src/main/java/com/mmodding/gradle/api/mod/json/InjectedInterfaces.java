package com.mmodding.gradle.api.mod.json;

import org.gradle.api.Action;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class InjectedInterfaces implements Serializable {

	private final Map<String, Set<String>> iifs = new LinkedHashMap<>();

	public boolean isEmpty() {
		return this.iifs.isEmpty();
	}

	public void injectTo(String target, String iif) {
		String patched = target.replace(".", "/");
		this.iifs.putIfAbsent(patched, new HashSet<>());
		this.iifs.get(patched).add(iif.replace(".", "/"));
	}

	public void injectTo(String target, Set<String> iifs) {
		this.iifs.put(
			target.replace(".", "/"),
			iifs.stream().map(iif -> iif.replace(".", "/")).collect(Collectors.toSet())
		);
	}

	public void fill(CustomElement.CustomBlock custom, boolean isQuilt) {
		Action<CustomElement.CustomBlock> process = block -> {
			for (Map.Entry<String, Set<String>> entry : this.iifs.entrySet()) {
				block.withArray(entry.getKey(), array -> {
					for (String iif : entry.getValue()) {
						array.add(iif);
					}
				});
			}
		};
		if (isQuilt) {
			custom.withBlock("quilt_loom", loom -> loom.withBlock("injected_interfaces", process));
		}
		else {
			custom.withBlock("loom:injected_interfaces", process);
		}
	}
}
