package com.mmodding.gradle.api.mod.json;

import com.mmodding.gradle.api.mod.json.dependency.QuiltModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.QuiltAdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.QuiltSimpleDependencies;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

public class QuiltModJson extends ModJson<QuiltModDependency, QuiltAdvancedDependencies, QuiltSimpleDependencies> implements Serializable {

	private static final Set<String> OFFICIAL_KEYS = Set.of(
		"schema_version",
		"quilt_loader",
		"minecraft",
		"access_widener",
		"mixin"
	);

	private final Map<String, String> contributors = new LinkedHashMap<>();
	private final QuiltAdvancedDependencies dependencies = new QuiltAdvancedDependencies();
	private final QuiltSimpleDependencies recommendations = new QuiltSimpleDependencies();
	private final QuiltSimpleDependencies suggestions = new QuiltSimpleDependencies();
	private final QuiltSimpleDependencies conflicts = new QuiltSimpleDependencies();
	private final QuiltSimpleDependencies breakages = new QuiltSimpleDependencies();

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

	public Map<String, String> getContributors() {
		return this.contributors;
	}

	public void addContributor(String name, String role) {
		this.contributors.put(name, role);
	}

	@Override
	public QuiltAdvancedDependencies getDependencies() {
		return this.dependencies;
	}

	@Override
	public void withDependencies(Action<QuiltAdvancedDependencies> action) {
		action.execute(this.dependencies);
	}

	@Override
	public QuiltSimpleDependencies getRecommendations() {
		return this.recommendations;
	}

	@Override
	public void withRecommendations(Action<QuiltSimpleDependencies> action) {
		action.execute(this.recommendations);
	}

	@Override
	public QuiltSimpleDependencies getSuggestions() {
		return this.suggestions;
	}

	@Override
	public void withSuggestions(Action<QuiltSimpleDependencies> action) {
		action.execute(this.suggestions);
	}

	@Override
	public QuiltSimpleDependencies getConflicts() {
		return this.conflicts;
	}

	@Override
	public void withConflicts(Action<QuiltSimpleDependencies> action) {
		action.execute(this.conflicts);
	}

	@Override
	public QuiltSimpleDependencies getBreakages() {
		return this.breakages;
	}

	@Override
	public void withBreakages(Action<QuiltSimpleDependencies> action) {
		action.execute(this.breakages);
	}

	@Override
	public void writeJson(Path path) throws IOException {
		JsonWriter writer = JsonWriter.json(path);
		writer.beginObject();

		writer.name("schema_version").value(1);

		{
			writer.name("quilt_loader")
				.beginObject()
				.name("group").value(Objects.requireNonNull(this.group, "Missing group in mod json declaration."))
				.name("id").value(Objects.requireNonNull(this.namespace, "Missing namespace/mod ID in mod json declaration."))
				.name("version").value(Objects.requireNonNull(this.version, "Missing version in mod json declaration."));

			if(this.name != null && this.description != null && this.contact.notEmpty()) {
				writer.name("metadata")
					.beginObject();

				if (this.name != null) {
					writer.name("name").value(this.name);
				}

				if (this.description != null) {
					writer.name("description").value(this.description);
				}

				if (this.icon != null) {
					writer.name("icon").value(this.icon);
				}

				if (this.license != null) {
					writer.name("license").value(this.license);
				}

				if (!this.contributors.isEmpty()) {
					writer.name("contributors").beginObject();
					for (Map.Entry<String, String> contributor : this.contributors.entrySet()) {
						writer.name(contributor.getKey()).value(contributor.getValue());
					}
					writer.endObject();
				}

				this.contact.writeJsonIfHavingContent(writer);

				writer.endObject();
			}

			if (this.intermediateMappings != null) {
				writer.name("intermediate_mappings").value(this.intermediateMappings);
			}

			if (!this.entrypoints.isEmpty()) {
				writer.name("entrypoints");
				this.entrypoints.writeJson(writer);
			}

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
				this.recommendations.writeJson(writer);
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

			writer.endObject();
		}

		{
			writer.name("minecraft").beginObject()
				.name("environment").value(this.environment.getQualifier())
				.endObject();
		}

		if (this.accessWidener != null) {
			writer.name("access_widener").value(this.accessWidener);
		}

		if (!this.mixins.isEmpty()) {
			writer.name("mixin").beginArray();
			for (MixinFile mixin : this.mixins) {
				mixin.writeJson(writer);
			}
			writer.endArray();
		}

		if (!this.injectedInterfaces.isEmpty()) {
			this.injectedInterfaces.fill(this.custom, true);
		}

		if (!this.parent.isEmpty()) {
			this.parent.fill(this.custom);
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
