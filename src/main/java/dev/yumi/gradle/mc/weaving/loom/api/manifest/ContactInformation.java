package dev.yumi.gradle.mc.weaving.loom.api.manifest;

import com.google.gson.JsonObject;

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

	public JsonObject toJson() {
		var json = new JsonObject();

		for (var field : FIELDS.entrySet()) {
			var value = (String) field.getValue().get(this);

			if (value != null) {
				json.addProperty(field.getKey(), value);
			}
		}

		return json;
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
