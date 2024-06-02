package com.mmodding.gradle.api.mod.json;

import com.mmodding.gradle.api.mod.json.dependency.QuiltModDependencies;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class QuiltModJson extends ModJson<QuiltModDependencies.QuiltModDependency, QuiltModDependencies> implements Serializable {

	private static final Set<String> OFFICIAL_KEYS = Set.of(
		"schema_version",
		"quilt_loader",
		"minecraft",
		"access_widener",
		"mixin"
	);

	private final List<Person> authors = new ArrayList<>();
	private final List<Person> contributors = new ArrayList<>();
	private final QuiltModDependencies dependencies = new QuiltModDependencies();

	private String group;
	private String intermediateMappings = "net.fabricmc:intermediary";

	@Override
	public @NotNull String getFileName() {
		return "quilt.mod.json";
	}

	@Override
	public void fillDefaults(Project project) {
		super.fillDefaults(project);
		this.group = project.getGroup().toString();
	}

	public String getGroup() {
		return this.group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getIntermediateMappings() {
		return this.intermediateMappings;
	}

	public void setIntermediateMappings(String intermediateMappings) {
		this.intermediateMappings = intermediateMappings;
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
	public QuiltModDependencies getDependencies() {
		return this.dependencies;
	}

	@Override
	public void withDependencies(Action<QuiltModDependencies> action) {
		action.execute(this.dependencies);
	}

	@Override
	public void writeJson(Path path) throws IOException {
		JsonWriter writer = JsonWriter.json(path);
		writer.beginObject();

		writer.name("schema_version").value(1);

		{
			writer.name("quilt_loader")
				.beginObject()
				.name("group").value(Objects.requireNonNull(this.group, "Missing group in manifest declaration."))
				.name("id").value(Objects.requireNonNull(this.namespace, "Missing namespace/mod ID in manifest declaration."))
				.name("version").value(Objects.requireNonNull(this.version, "Missing version in manifest declaration."));

			if(this.name != null && this.description != null && this.contact.notEmpty()) {
				writer.name("metadata")
					.beginObject();

				if (this.name != null) {
					writer.name("name").value(this.name);
				}

				if (this.description != null) {
					writer.name("description").value(this.description);
				}

				this.contact.writeJsonIfHavingContent(writer);

				writer.endObject();
			}

			if (!this.dependencies.isEmpty()) {
				writer.name("depends");
				this.dependencies.writeJson(writer);
			}

			writer.endObject();
		}

		if (this.intermediateMappings != null) {
			writer.name("intermediate_mappings").value(this.intermediateMappings);
		}

		if (this.license != null) {
			writer.name("license").value(this.license);
		}

		{
			writer.name("minecraft").beginObject()
				.name("environment").value(this.environment.getManifestName())
				.endObject();
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

		if (this.accessWidener != null) {
			writer.name("access_widener").value(this.accessWidener);
		}

		if (!this.mixins.isEmpty()) {
			writer.name("mixins").beginObject();
			for (MixinFile mixin : this.mixins) {
				mixin.writeJson(writer);
			}
			writer.endObject();
		}

		if (!this.custom.isEmpty()) {
			this.custom.forEach((key, value) -> {
				if (!OFFICIAL_KEYS.contains(key)) {
					writer.name(key);
					value.writeJson(writer);
				}
			});
		}

		writer.endObject();
		writer.close();
	}
}
