package com.mmodding.gradle.api.mod.json;

import com.mmodding.gradle.api.EnvironmentTarget;
import com.mmodding.gradle.api.mod.json.dependency.ModDependencies;
import groovy.transform.Internal;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class ModJson<R extends ModDependencies.ModDependency, D extends ModDependencies<R>> implements Serializable {

	protected String namespace;
	protected String name;
	protected String version;
	protected String description;
	protected String license;
	protected String icon;
	protected EnvironmentTarget environment = EnvironmentTarget.ANY;
	protected final ModEntrypoints entrypoints = new ModEntrypoints(this instanceof QuiltModJson);
	protected final ContactInformation contact = new ContactInformation();
	protected final List<MixinFile> mixins = new ArrayList<>();
	protected String accessWidener;
	protected final InjectedInterfaces injectedInterfaces = new InjectedInterfaces();
	protected final CustomElement.CustomBlock custom = new CustomElement.CustomBlock();

	public void fillDefaults(Project project) {
		this.name = project.getName();
		this.version = project.getVersion().toString();
	}

	@Internal
	@Contract(pure = true)
	public abstract @NotNull String getFileName();

	public String getNamespace() {
		return this.namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLicense() {
		return this.license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public EnvironmentTarget getEnvironment() {
		return this.environment;
	}

	public void setEnvironment(EnvironmentTarget environment) {
		this.environment = environment;
	}

	public ContactInformation getContact() {
		return this.contact;
	}

	public void withContact(Action<ContactInformation> action) {
		action.execute(this.contact);
	}

	public abstract D getDependencies();

	public abstract void withDependencies(Action<D> action);

	public List<MixinFile> getMixins() {
		return this.mixins;
	}

	public void addMixin(String file) {
		this.mixins.add(new MixinFile(file));
	}

	public void addMixin(String file, EnvironmentTarget environment) {
		this.mixins.add(new MixinFile(file, environment));
	}

	public String getAccessWidener() {
		return this.accessWidener;
	}

	public void setAccessWidener(String accessWidener) {
		this.accessWidener = accessWidener;
	}

	public InjectedInterfaces getInjectedInterfaces() {
		return this.injectedInterfaces;
	}

	public void withInjectedInterfaces(Action<InjectedInterfaces> action) {
		action.execute(this.injectedInterfaces);
	}

	public CustomElement.CustomBlock getCustom() {
		return this.custom;
	}

	public void withCustom(Action<CustomElement.CustomBlock> action) {
		action.execute(this.custom);
	}

	public abstract void writeJson(Path path) throws IOException;
}