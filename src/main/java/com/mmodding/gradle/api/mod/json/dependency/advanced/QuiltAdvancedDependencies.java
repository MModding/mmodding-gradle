package com.mmodding.gradle.api.mod.json.dependency.advanced;

import com.mmodding.gradle.api.mod.json.dependency.QuiltModDependency;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;

public class QuiltAdvancedDependencies extends AdvancedDependencies<QuiltModDependency> {

	private String quiltLoaderVersion = null;
	private String quiltedFabricApiVersion = null;

	@Override
	public boolean isEmpty() {
		return this.isRootEmpty() && this.quiltLoaderVersion == null && this.quiltedFabricApiVersion == null;
	}

	public String getQuiltLoaderVersion() {
		return this.quiltLoaderVersion;
	}

	public void setQuiltLoaderVersion(String quiltLoaderVersion) {
		this.quiltLoaderVersion = quiltLoaderVersion;
	}

	public String getQuiltedFabricApiVersion() {
		return this.quiltedFabricApiVersion;
	}

	public void setQuiltedFabricApiVersion(String quiltedFabricApiVersion) {
		this.quiltedFabricApiVersion = quiltedFabricApiVersion;
	}

	@Override
	public void addDependency(String namespace, String version) {
		this.otherDependencies.add(new QuiltModDependency(namespace, version));
	}

	@Override
	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginArray();
		if (this.javaVersion != null) {
			new QuiltModDependency("java", this.javaVersion).writeJson(writer);
		}
		if (this.minecraftVersion != null) {
			new QuiltModDependency("minecraft", this.minecraftVersion).writeJson(writer);
		}
		if (this.quiltLoaderVersion != null) {
			new QuiltModDependency("quilt_loader", this.quiltLoaderVersion).writeJson(writer);
		}
		if (this.quiltedFabricApiVersion != null) {
			new QuiltModDependency("quilted_fabric_api", this.quiltedFabricApiVersion).writeJson(writer);
		}
		if (this.mmoddingLibraryVersion != null) {
			new QuiltModDependency("mmodding", this.mmoddingLibraryVersion).writeJson(writer);
		}
		for (QuiltModDependency dependency : this.otherDependencies) {
			dependency.writeJson(writer);
		}
		writer.endArray();
	}
}
