package com.mmodding.gradle.api;

public enum EnvironmentTarget {
	ANY("*"),
	CLIENT("client"),
	DEDICATED_SERVER("server");

	private final String qualifier;

	EnvironmentTarget(String qualifier) {
		this.qualifier = qualifier;
	}

	public String getQualifier() {
		return this.qualifier;
	}
}
