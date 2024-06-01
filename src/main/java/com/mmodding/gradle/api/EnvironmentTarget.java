package com.mmodding.gradle.api;

public enum EnvironmentTarget {
	ANY("*"),
	CLIENT("client"),
	DEDICATED_SERVER("server");

	private final String manifestName;

	EnvironmentTarget(String manifestName) {
		this.manifestName = manifestName;
	}

	public String getManifestName() {
		return this.manifestName;
	}
}
