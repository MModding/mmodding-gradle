package com.mmodding.gradle.api.mod.json.dependency.advanced;

import com.mmodding.gradle.api.mod.json.dependency.ModDependency;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class AdvancedDependencies<D extends ModDependency> implements Serializable {

	protected String javaVersion = null;
	protected String minecraftVersion = null;
	protected String mmoddingLibraryVersion = null;

	protected final Set<D> otherDependencies = new HashSet<>();

	protected boolean isRootEmpty() {
		return this.javaVersion == null && this.minecraftVersion == null && this.mmoddingLibraryVersion == null && this.otherDependencies.isEmpty();
	}

	public abstract boolean isEmpty();

	public String getJavaVersion() {
		return this.javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getMinecraftVersion() {
		return this.minecraftVersion;
	}

	public void setMinecraftVersion(String minecraftVersion) {
		this.minecraftVersion = minecraftVersion;
	}

	public String getMmoddingLibraryVersion() {
		return this.mmoddingLibraryVersion;
	}

	public void setMmoddingLibraryVersion(String mmoddingLibraryVersion) {
		this.mmoddingLibraryVersion = mmoddingLibraryVersion;
	}

	public void addDependency(String namespace) {
		this.addDependency(namespace, "*");
	}

	public abstract void addDependency(String namespace, String version);

	public abstract void writeJson(JsonWriter writer) throws IOException;
}
