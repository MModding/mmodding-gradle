package dev.yumi.gradle.mc.weaving.loom.api.manifest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.gradle.api.Action;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class Person implements Serializable {
	private final String name;
	private ContactInformation contact;

	public Person(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public ContactInformation getContact() {
		return this.contact;
	}

	public void setContact(ContactInformation contact) {
		this.contact = contact;
	}

	public void withContact(@NotNull Action<ContactInformation> action) {
		if (this.contact == null) {
			this.contact = new ContactInformation();
		}

		action.execute(this.contact);
	}

	public JsonElement toJson() {
		if (this.contact == null) {
			return new JsonPrimitive(this.name);
		} else {
			var json = new JsonObject();
			json.addProperty("name", this.name);
			json.add("contact", this.contact.toJson());
			return json;
		}
	}
}
