package dev.yumi.gradle.mc.weaving.loom.api.manifest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.yumi.gradle.mc.weaving.loom.api.EnvironmentTarget;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class QuiltModManifest extends ModManifest implements Serializable {
	private static final Set<String> OFFICIAL_KEYS = Set.of(
			"schema_version",
			"quilt_loader",
			"minecraft",
			"access_widener",
			"mixin"
	);
	private String group;
	private String intermediateMappings = "net.fabricmc:intermediary";
	private final List<Person> authors = new ArrayList<>();
	private final List<Person> contributors = new ArrayList<>();

	@Override
	public @NotNull String getFileName() {
		return "fabric.mod.json";
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
	public JsonObject toJson() {
		var json = new JsonObject();
		json.addProperty("schemaVersion", 1);

		{
			var quiltLoaderJson = new JsonObject();
			json.add("quilt_loader", quiltLoaderJson);

			quiltLoaderJson.addProperty("group",
					Objects.requireNonNull(this.group, "Missing group in manifest declaration.")
			);

			quiltLoaderJson.addProperty("id",
					Objects.requireNonNull(this.namespace, "Missing namespace/mod ID in manifest declaration.")
			);

			quiltLoaderJson.addProperty("version",
					Objects.requireNonNull(this.version, "Missing version in manifest declaration.")
			);

			{
				var metadata = new JsonObject();

				if (this.name != null) {
					metadata.addProperty("name", this.name);
				}

				if (this.description != null) {
					metadata.addProperty("description", this.description);
				}

				var contactJson = this.contact.toJson();
				if (!contactJson.isEmpty()) {
					metadata.add("contact", contactJson);
				}

				if (this.intermediateMappings != null) {
					quiltLoaderJson.addProperty("intermediate_mappings", this.intermediateMappings);
				}

				if (!metadata.isEmpty()) {
					quiltLoaderJson.add("metadata", metadata);
				}
			}
		}

		if (this.license != null) {
			json.addProperty("license", this.license);
		}

		{
			var minecraftJson = new JsonObject();
			json.add("minecraft", minecraftJson);

			if (this.environment != EnvironmentTarget.ANY) {
				minecraftJson.addProperty("environment", this.environment.getManifestName());
			}
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

		if (this.accessWidener != null) {
			json.addProperty("access_widener", this.accessWidener);
		}

		if (!this.mixins.isEmpty()) {
			json.add("mixin",
					this.mixins.stream()
							.map(MixinFile::toJson)
							.collect(JsonArray::new, JsonArray::add, JsonArray::addAll)
			);
		}

		if (!this.custom.isEmpty()) {
			this.custom.forEach((key, value) -> {
				if (!OFFICIAL_KEYS.contains(key)) {
					json.add(key, value.toJson());
				}
			});
		}

		return json;
	}
}
