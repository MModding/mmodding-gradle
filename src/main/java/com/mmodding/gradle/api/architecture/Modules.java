package com.mmodding.gradle.api.architecture;

import net.fabricmc.loom.util.Pair;
import org.gradle.api.Action;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import java.util.*;
import java.util.function.BiConsumer;

public class Modules {

	private final Set<String> apiModules = new HashSet<>();
	private final Set<String> implementationModules = new HashSet<>();
	private final Modules includeContainer;
	private final BiConsumer<DependencyHandler, Pair<String, String>> moduleProcessor;

	public Modules(boolean includeMode) {
		this.includeContainer = !includeMode ? new Modules(true) : null;
		this.moduleProcessor = (handler, module) -> {
			Map<String, String> notation = new HashMap<>();
			notation.put("path", ":" + module.right());
			notation.put("configuration", "namedElements");
			if (includeMode) {
				handler.add(module.left(), Objects.requireNonNull(handler.add("include", handler.project(notation))));
			}
			else {
				handler.add(module.left(), handler.project(notation));
			}
		};
	}

	public void api(String module) {
		this.apiModules.add(module);
	}

	public void implementation(String module) {
		this.implementationModules.add(module);
	}

	public void include(Action<Modules> action) {
		if (this.includeContainer != null) {
			action.execute(this.includeContainer);
		}
		else {
			throw new IllegalStateException("Cannot use include inside of an includeMode: true Modules object");
		}
	}

	public void apply(DependencyHandler handler) {
		for (String module : this.apiModules) {
			this.moduleProcessor.accept(handler, new Pair<>("api", module));
		}
		for (String module : this.implementationModules) {
			this.moduleProcessor.accept(handler, new Pair<>("implementation", module));
		}
		if (this.includeContainer != null) {
			if (!this.includeContainer.apiModules.isEmpty() || !this.includeContainer.implementationModules.isEmpty()) {
				this.includeContainer.apply(handler);
			}
		}
	}
}
