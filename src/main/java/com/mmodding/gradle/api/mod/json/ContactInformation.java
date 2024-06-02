package com.mmodding.gradle.api.mod.json;

import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ContactInformation implements Serializable {

	/**
	 * Contact e-mail pertaining to the mod. Must be a valid e-mail address.
	 */
	private String email;
	/**
	 * IRC channel pertaining to the mod.
	 * Must be of a valid URL format - for example: irc://irc.esper.net:6667/charset for #charset at EsperNet - the port is optional,
	 * and assumed to be 6667 if not present.
	 */
	private String irc;
	/**
	 * Project or user homepage. Must be a valid HTTP/HTTPS address.
	 */
	private String homepage;
	/**
	 * Project issue tracker. Must be a valid HTTP/HTTPS address.
	 */
	private String issues;
	/**
	 * Project source code repository.
	 * Must be a valid URL - it can, however, be a specialized URL for a given VCS (such as Git or Mercurial).
	 */
	private String sources;
	private String discord;
	private String twitter;

	private static final Map<String, VarHandle> FIELDS;

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIrc() {
		return this.irc;
	}

	public void setIrc(String irc) {
		this.irc = irc;
	}

	public String getHomepage() {
		return this.homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getIssues() {
		return this.issues;
	}

	public void setIssues(String issues) {
		this.issues = issues;
	}

	public String getSources() {
		return this.sources;
	}

	public void setSources(String sources) {
		this.sources = sources;
	}

	public String getDiscord() {
		return this.discord;
	}

	public void setDiscord(String discord) {
		this.discord = discord;
	}

	public String getTwitter() {
		return this.twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public boolean notEmpty() {
		Map<String, String> entries = new HashMap<>();

		for (var field : FIELDS.entrySet()) {
			var value = (String) field.getValue().get(this);

			if (value != null) {
				entries.put(field.getKey(), value);
			}
		}

		return !entries.isEmpty();
	}

	public void writeJsonIfHavingContent(JsonWriter writer) throws IOException {
		Map<String, String> entries = new HashMap<>();

		for (var field : FIELDS.entrySet()) {
			var value = (String) field.getValue().get(this);

			if (value != null) {
				entries.put(field.getKey(), value);
			}
		}

		if (!entries.isEmpty()) {
			writer.name("contact").beginObject();
			for (Map.Entry<String, String> entry : entries.entrySet()) {
				writer.name(entry.getKey()).value(entry.getValue());
			}
			writer.endObject();
		}
	}

	public void writeJson(JsonWriter writer) throws IOException {
		writer.beginObject();

		for (var field : FIELDS.entrySet()) {
			var value = (String) field.getValue().get(this);

			if (value != null) {
				writer.name(field.getKey()).value(value);
			}
		}

		writer.endObject();
	}

	static {
		FIELDS = new HashMap<>();

		for (var field : ContactInformation.class.getDeclaredFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				try {
					FIELDS.put(field.getName(), MethodHandles.lookup().unreflectVarHandle(field));
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}
