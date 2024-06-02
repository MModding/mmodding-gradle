package com.mmodding.gradle.api.mod.json;

import org.gradle.api.Action;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.parsers.json.JsonWriter;

import java.io.IOException;
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

	public void writeJson(JsonWriter writer) throws IOException {
		if (this.contact == null) {
			writer.value(this.name);
		} else {
			writer.beginObject()
				.name("name").value(this.name)
				.name("contact");
			this.contact.writeJson(writer);
			writer.endObject();
		}
	}
}
