package com.mmodding.gradle.api.mod.json;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ParentSchema implements Serializable {

	private final String namespace;
	private final Set<String> badges = new HashSet<>();

	private String name = null;
	private String description = null;
	private String icon = null;

	public ParentSchema(String namespace) {
		this.namespace = namespace;
	}

	public boolean isEmpty() {
		return this.namespace == null;
	}

	public String getNamespace() {
		return this.namespace;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Set<String> getBadges() {
		return this.badges;
	}

	public void addBadge(String badge) {
		this.badges.add(badge);
	}

	public void addBadges(Set<String> badges) {
		this.badges.addAll(badges);
	}

	public void fill(CustomElement.CustomBlock custom) {
		if (this.name != null || this.description != null || this.icon != null || !this.badges.isEmpty()) {
			custom.putBlock("modmenu", modmenu ->
				modmenu.putBlock("parent", parent -> {
					parent.put("id", this.namespace);
					if (this.name != null) {
						parent.put("name", this.name);
					}
					if (this.description != null) {
						parent.put("description", this.description);
					}
					if (this.icon != null) {
						parent.put("icon", this.icon);
					}
					if (!this.badges.isEmpty()) {
						parent.putArray("badges", array -> this.badges.forEach(array::addUnique));
					}
				})
			);
		}
		else {
			custom.putBlock("modmenu", modmenu ->
				modmenu.put("parent", this.namespace)
			);
		}
	}
}
