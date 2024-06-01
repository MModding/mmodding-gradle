package dev.yumi.gradle.mc.weaving.loom.api.manifest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.yumi.gradle.mc.weaving.loom.api.EnvironmentTarget;
import org.gradle.api.Action;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FabricModManifest extends ModManifest implements Serializable {
	private final List<Person> authors = new ArrayList<>();
	private final List<Person> contributors = new ArrayList<>();

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
	public JsonObject toJson() {
		var json = new JsonObject();
		json.addProperty("schemaVersion", 1);
		json.addProperty("id", Objects.requireNonNull(this.namespace, "Missing namespace/mod ID in manifest declaration."));

		if (this.name != null) {
			json.addProperty("name", this.name);
		}

		json.addProperty("version", Objects.requireNonNull(this.version, "Missing version in manifest declaration."));

		if (this.description != null) {
			json.addProperty("description", this.description);
		}

		if (this.license != null) {
			json.addProperty("license", this.license);
		}

		if (this.environment != EnvironmentTarget.ANY) {
			json.addProperty("environment", this.environment.getManifestName());
		}

		if (!this.authors.isEmpty()) {
			var authorsJson = new JsonArray();
			this.authors.forEach(person -> authorsJson.add(person.toJson()));
			json.add("authors", authorsJson);
		}

		if (!this.contributors.isEmpty()) {
			var contributorsJson = new JsonArray();
			this.contributors.forEach(person -> contributorsJson.add(person.toJson()));
			json.add("contributors", contributorsJson);
		}

		var contactJson = this.contact.toJson();
		if (!contactJson.isEmpty()) {
			json.add("contact", contactJson);
		}

		if (this.accessWidener != null) {
			json.addProperty("accessWidener", this.accessWidener);
		}

		if (!this.mixins.isEmpty()) {
			json.add("mixins",
					this.mixins.stream()
							.map(MixinFile::toJson)
							.collect(JsonArray::new, JsonArray::add, JsonArray::addAll)
			);
		}

		if (!this.custom.isEmpty()) {
			json.add("custom", this.custom.toJson());
		}

		return json;
	}
}
