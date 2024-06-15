package com.mmodding.gradle.api.mod.json.dependency;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;

public class FabricModDependencies extends ModDependencies<FabricModDependencies.FabricModDependency> {

	private String fabricLoaderVersion = null;
	private String fabricApiVersion = null;

	@Override
	public boolean isEmpty() {
		return this.isRootEmpty() && this.fabricLoaderVersion == null && this.fabricApiVersion == null;
	}

	public String getFabricLoaderVersion() {
		return this.fabricLoaderVersion;
	}

	public void setFabricLoaderVersion(String fabricLoaderVersion) {
		this.fabricLoaderVersion = fabricLoaderVersion;
	}

	public String getFabricApiVersion() {
		return this.fabricApiVersion;
	}

	public void setFabricApiVersion(String fabricApiVersion) {
		this.fabricApiVersion = fabricApiVersion;
	}

	@Override
	public void addDependency(String namespace, String version) {
		this.otherDependencies.add(new FabricModDependency(namespace, version));
	}

	@Override
	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginObject();
		if (this.javaVersion != null) {
			new FabricModDependency("java", this.javaVersion).writeJson(writer);
		}
		if (this.minecraftVersion != null) {
			new FabricModDependency("minecraft", this.minecraftVersion).writeJson(writer);
		}
		if (this.fabricLoaderVersion != null) {
			new FabricModDependency("fabricloader", this.fabricLoaderVersion).writeJson(writer);
		}
		if (this.fabricApiVersion != null) {
			new FabricModDependency("fabric-api", this.fabricApiVersion).writeJson(writer);
		}
		if (this.mmoddingLibraryVersion != null) {
			new FabricModDependency("mmodding", this.mmoddingLibraryVersion).writeJson(writer);
		}
		for (FabricModDependency dependency : this.otherDependencies) {
			dependency.writeJson(writer);
		}
		writer.endObject();
	}

	public static class FabricModDependency extends ModDependency {

		public FabricModDependency(String namespace, String version) {
			super(namespace, version);
		}

		@Override
		public void writeJson(JsonWriter writer) throws IOException {
			writer.name(this.namespace).value(this.version);
		}
	}
}
