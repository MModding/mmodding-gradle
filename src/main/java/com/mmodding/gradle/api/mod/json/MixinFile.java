package com.mmodding.gradle.api.mod.json;

import com.mmodding.gradle.api.EnvironmentTarget;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;

public class MixinFile implements Serializable {

	private String file;
	private EnvironmentTarget environment;

	public MixinFile(String file) {
		this(file, EnvironmentTarget.ANY);
	}

	public MixinFile(String file, EnvironmentTarget environment) {
		this.file = file;
		this.environment = environment;
	}

	public String getFile() {
		return this.file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public EnvironmentTarget getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(EnvironmentTarget environment) {
		this.environment = environment;
	}

	public void writeJson(JsonWriter writer) throws IOException {
		if (this.environment != EnvironmentTarget.ANY) {
			writer.beginObject();
			writer.name("config").value(this.file);
			writer.name("environment").value(this.environment.getQualifier());
			writer.endObject();
		} else {
			writer.value(this.file);
		}
	}
}
