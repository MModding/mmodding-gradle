package com.mmodding.gradle.api.mod.json;

import com.mmodding.gradle.api.EnvironmentTarget;
import com.mmodding.gradle.api.mod.json.dependency.FabricModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.FabricAdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.FabricSimpleDependencies;
import org.gradle.api.Action;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.quiltmc.parsers.json.JsonWriter;

public class FabricModJson extends ModJson<FabricModDependency, FabricAdvancedDependencies, FabricSimpleDependencies> implements Serializable {

	private final List<Person> authors = new ArrayList<>();
	private final List<Person> contributors = new ArrayList<>();
	private final FabricAdvancedDependencies dependencies = new FabricAdvancedDependencies();
	private final FabricSimpleDependencies recommendations = new FabricSimpleDependencies();
	private final FabricSimpleDependencies suggestions = new FabricSimpleDependencies();
	private final FabricSimpleDependencies conflicts = new FabricSimpleDependencies();
	private final FabricSimpleDependencies breakages = new FabricSimpleDependencies();

	@Override
	public @NotNull String getFileName() {
		return "fabric.mod.json";
	}

	public List<Person> getAuthors() {
		return this.authors;
	}

	public void addAuthor(String name) {
		this.authors.add(new Person(name));
	}

	public void addAuthor(String name, Action<Person> action) {
		var person = new Person(name);
		action.execute(person);
		this.authors.add(person);
	}

	public List<Person> getContributors() {
		return this.contributors;
	}

	public void addContributor(String name) {
		this.contributors.add(new Person(name));
	}

	public void addContributor(String name, Action<Person> action) {
		var person = new Person(name);
		action.execute(person);
		this.contributors.add(person);
	}

	@Override
	public FabricAdvancedDependencies getDependencies() {
		return this.dependencies;
	}

	@Override
	public void withDependencies(Action<FabricAdvancedDependencies> action) {
		action.execute(this.dependencies);
	}

	@Override
	public FabricSimpleDependencies getRecommendations() {
		return this.recommendations;
	}

	@Override
	public void withRecommendations(Action<FabricSimpleDependencies> action) {
		action.execute(this.recommendations);
	}

	@Override
	public FabricSimpleDependencies getSuggestions() {
		return this.suggestions;
	}

	@Override
	public void withSuggestions(Action<FabricSimpleDependencies> action) {
		action.execute(this.suggestions);
	}

	@Override
	public FabricSimpleDependencies getConflicts() {
		return this.conflicts;
	}

	@Override
	public void withConflicts(Action<FabricSimpleDependencies> action) {
		action.execute(this.conflicts);
	}

	@Override
	public FabricSimpleDependencies getBreakages() {
		return this.breakages;
	}

	@Override
	public void withBreakages(Action<FabricSimpleDependencies> action) {
		action.execute(this.breakages);
	}

	@Override
	public void writeJson(Path path) throws IOException {
		JsonWriter writer = JsonWriter.json(path);
		writer.beginObject();

		writer.name("schemaVersion").value(1)
			.name("id").value(Objects.requireNonNull(this.namespace, "Missing namespace/mod ID in mod json declaration."));

		if (this.name != null) {
			writer.name("name").value(this.name);
		}

		writer.name("version").value(Objects.requireNonNull(this.version, "Missing version in mod json declaration."));

		if (this.description != null) {
			writer.name("description").value(this.description);
		}

		if (this.icon != null) {
			writer.name("icon").value(this.icon);
		}

		if (this.license != null) {
			writer.name("license").value(this.license);
		}

		if (this.environment != EnvironmentTarget.ANY) {
			writer.name("environment").value(this.environment.getQualifier());
		}

		if (!this.entrypoints.isEmpty()) {
			writer.name("entrypoints");
			this.entrypoints.writeJson(writer);
		}

		if (!this.authors.isEmpty()) {
			writer.name("authors").beginArray();
			for (Person author : this.authors) {
				author.writeJson(writer);
			}
			writer.endArray();
		}

		if (!this.contributors.isEmpty()) {
			writer.name("contributors").beginArray();
			for (Person contributor : this.contributors) {
				contributor.writeJson(writer);
			}
			writer.endArray();
		}

		this.contact.writeJsonIfHavingContent(writer);

		if (!this.dependencies.isEmpty()) {
			writer.name("depends");
			this.dependencies.writeJson(writer);
		}

		if (!this.recommendations.isEmpty()) {
			writer.name("recommends");
			this.recommendations.writeJson(writer);
		}

		if (!this.suggestions.isEmpty()) {
			writer.name("suggests");
			this.suggestions.writeJson(writer);
		}

		if (!this.conflicts.isEmpty()) {
			writer.name("conflicts");
			this.conflicts.writeJson(writer);
		}

		if (!this.breakages.isEmpty()) {
			writer.name("breaks");
			this.breakages.writeJson(writer);
		}

		if (!this.provider.isEmpty()) {
			writer.name("provides");
			this.provider.writeJson(writer);
		}

		if (this.accessWidener != null) {
			writer.name("accessWidener").value(this.accessWidener);
		}

		if (!this.mixins.isEmpty()) {
			writer.name("mixins").beginArray();
			for (MixinFile mixin : this.mixins) {
				mixin.writeJson(writer);
			}
			writer.endArray();
		}

		if (!this.injectedInterfaces.isEmpty()) {
			this.injectedInterfaces.fill(this.custom, false);
		}

		if (!this.parent.isEmpty()) {
			this.parent.fill(this.custom);
		}

		if (!this.custom.isEmpty()) {
			writer.name("custom");
			this.custom.writeJson(writer);
		}

		writer.endObject();
		writer.close();
	}
}
