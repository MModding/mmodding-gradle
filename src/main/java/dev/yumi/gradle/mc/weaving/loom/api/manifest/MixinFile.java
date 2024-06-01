package dev.yumi.gradle.mc.weaving.loom.api.manifest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.yumi.gradle.mc.weaving.loom.api.EnvironmentTarget;

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

	public JsonElement toJson() {
		if (this.environment != EnvironmentTarget.ANY) {
			var json = new JsonObject();
			json.addProperty("config", this.file);
			json.addProperty("environment", this.environment.getManifestName());
			return json;
		} else {
			return new JsonPrimitive(this.file);
		}
	}
}
