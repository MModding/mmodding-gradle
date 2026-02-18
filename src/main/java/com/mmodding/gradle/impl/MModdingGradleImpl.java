package com.mmodding.gradle.impl;

import com.mmodding.gradle.api.MModdingGradle;
import com.mmodding.gradle.api.architecture.Modules;
import com.mmodding.gradle.api.mod.json.FabricModJson;
import com.mmodding.gradle.api.mod.json.ModJson;
import com.mmodding.gradle.api.mod.json.QuiltModJson;
import com.mmodding.gradle.api.mod.json.dependency.ModDependency;
import com.mmodding.gradle.api.mod.json.dependency.advanced.AdvancedDependencies;
import com.mmodding.gradle.api.mod.json.dependency.simple.SimpleDependencies;
import com.mmodding.gradle.api.testmod.TestModJson;
import com.mmodding.gradle.impl.task.GenerateFabricModJson;
import com.mmodding.gradle.impl.task.GenerateModJson;
import com.mmodding.gradle.impl.testmod.TestModJsonImpl;
import com.mmodding.gradle.impl.util.LoomProvider;
import com.mmodding.gradle.impl.util.TaskType;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSet;
import org.gradle.jvm.tasks.Jar;

import javax.inject.Inject;
import java.util.function.Predicate;

public class MModdingGradleImpl implements MModdingGradle {

	private final Project project;
	private final LoomProvider loomProvider;

	@Inject
	public MModdingGradleImpl(final ObjectFactory objects, final Project project) {
		this.project = project;
		this.loomProvider = new LoomProvider(project);
	}

	private <D extends ModDependency, A extends AdvancedDependencies<D>, S extends SimpleDependencies<D>, M extends ModJson<D, A, S>> void configureModJson(TaskType<D, A, S, M> taskType, Action<M> action) {
		if (this.project.getTasks().findByPath(taskType.getTaskName()) == null) {
			M modJson = taskType.createModJson();
			modJson.fillDefaults(this.project);
			action.execute(modJson);

			// Setup Generation Task
			GenerateModJson<D, A, S, M> task = this.project.getTasks().create(taskType.getTaskName(), taskType.getTaskClass(), false);
			task.getModJson().set(modJson);
			this.project.getTasks().getByPath("ideaSyncTask").dependsOn(task);

			// Add Resource Output Directory to Main Resources
			JavaPluginExtension javaExtension = this.project.getExtensions().getByType(JavaPluginExtension.class);
			SourceSet mainSourceSet = javaExtension.getSourceSets().getByName("main");
			mainSourceSet.getResources().srcDir(task);
		}
		else {
			// Behaving differently if the FabricModJson already exists
			@SuppressWarnings("unchecked")
			GenerateModJson<D, A, S, M> task = (GenerateModJson<D, A, S, M>) this.project.getTasks().getByName(taskType.getTaskName());
			M modJson = task.getModJson().get();
			action.execute(modJson);
			task.getModJson().set(modJson);
		}
	}

	private <D extends ModDependency, A extends AdvancedDependencies<D>, S extends SimpleDependencies<D>, M extends ModJson<D, A, S>> Dependency configureModJsonForDependency(Dependency dependency, TaskType<D, A, S, M> taskType, Action<M> action) {
		CustomModJsonGenerationTask task = (CustomModJsonGenerationTask) this.project.getTasks().findByPath(taskType.getDependencyName());
		assert task != null;

		M modJson = taskType.createModJson();
		modJson.setName(dependency.getName());
		modJson.setVersion(dependency.getVersion());

		action.execute(modJson);
		task.addModJson(new CustomModJsonGenerationTask.Metadata(dependency.getGroup(), dependency.getName(), dependency.getVersion()), modJson);

		return dependency;
	}

	public void configureFabricModJson(Action<FabricModJson> action) {
		this.configureModJson(TaskType.FMJ, action);
	}

	public void configureQuiltModJson(Action<QuiltModJson> action) {
		this.configureModJson(TaskType.QMJ, action);
	}

	public Dependency configureFMJForDependency(Dependency dependency, Action<FabricModJson> action) {
		return this.configureModJsonForDependency(dependency, TaskType.FMJ, action);
	}

	public Dependency configureQMJForDependency(Dependency dependency, Action<QuiltModJson> action) {
		return this.configureModJsonForDependency(dependency, TaskType.QMJ, action);
	}

	@Override
	public void configureTestmod(Action<TestModJson> action) {
		if (this.project.getTasks().findByPath("generateTestModJson") == null) { // Check if initial setup already happened.
			JavaPluginExtension javaExtension = this.project.getExtensions().getByType(JavaPluginExtension.class);
			SourceSet mainSourceSet = javaExtension.getSourceSets().getByName("main");
			SourceSet testmodSourceSet = javaExtension.getSourceSets().maybeCreate("testmod");
			SourceSet testSourceSet = javaExtension.getSourceSets().maybeCreate("test");

			// Source Sets Setup
			testmodSourceSet.setCompileClasspath(testmodSourceSet.getCompileClasspath().plus(mainSourceSet.getCompileClasspath()));
			testmodSourceSet.setRuntimeClasspath(testmodSourceSet.getRuntimeClasspath().plus(mainSourceSet.getRuntimeClasspath()));
			testSourceSet.setCompileClasspath(testSourceSet.getCompileClasspath().plus(testmodSourceSet.getCompileClasspath()));
			testSourceSet.setRuntimeClasspath(testSourceSet.getRuntimeClasspath().plus(testmodSourceSet.getRuntimeClasspath()));

			// Loom DSL Configuration
			this.loomProvider.getLoom().getRuntimeOnlyLog4j().set(true);
			this.loomProvider.getLoom().getRuns().register("testmodClient", settings -> {
				settings.client();
				settings.ideConfigGenerated(this.project.getRootProject() == this.project);
				settings.name("Testmod Client");
				settings.source(testmodSourceSet);
			});
			this.loomProvider.getLoom().getRuns().register("testmodServer", settings -> {
				settings.server();
				settings.ideConfigGenerated(this.project.getRootProject() == this.project);
				settings.name("Testmod Server");
				settings.source(testmodSourceSet);
			});

			// Jar Task Configuration
			this.project.getTasks().register("testmodJar", Jar.class).configure(task -> {
				task.from(testmodSourceSet.getOutput());
				task.getArchiveClassifier().set("testmod");
			});
			this.project.getDependencies().add("testmodImplementation", mainSourceSet.getOutput());

			// FMJ Generation Task Configuration - that's a testmod, we don't make it really advanced
			TestModJsonImpl testModJson = new TestModJsonImpl();
			testModJson.setName(this.project.getName().replace("-", "_") + "_testmod");
			testModJson.setVersion("1.0.0");
			action.execute(testModJson);

			GenerateFabricModJson generateTestModJsonTask = this.project.getTasks().create("generateTestModJson", GenerateFabricModJson.class, true);
			generateTestModJsonTask.getModJson().set(testModJson);
			this.project.getTasks().getByPath("ideaSyncTask").dependsOn(generateTestModJsonTask);

			testmodSourceSet.getResources().srcDir(generateTestModJsonTask);
		}
		else {
			GenerateFabricModJson generateTestModJsonTask = (GenerateFabricModJson) this.project.getTasks().getByName("generateTestModJson");
			TestModJsonImpl testModJson = (TestModJsonImpl) generateTestModJsonTask.getModJson().get();
			action.execute(testModJson);
			generateTestModJsonTask.getModJson().set(testModJson);
		}
	}

	@Override
	public void loomModRegistration() {
		this.loomModRegistration(current -> true);
	}

	@Override
	public void loomModRegistration(Predicate<Project> filter) {
		this.project.allprojects(current -> {
			if (filter.test(current)) {
				JavaPluginExtension ext = current.getExtensions().findByType(JavaPluginExtension.class);
				if (ext != null) {
					this.loomProvider.getLoom().getMods().register(current.getName())
						.configure(settings -> settings.sourceSet(ext.getSourceSets().getByName("main")));
				}
			}
		});
	}

	@Override
	public void loomTestmodRegistration() {
		this.loomTestmodRegistration(current -> true);
	}

	@Override
	public void loomTestmodRegistration(Predicate<Project> filter) {
		this.project.allprojects(current -> {
			if (filter.test(current)) {
				JavaPluginExtension ext = current.getExtensions().findByType(JavaPluginExtension.class);
				if (ext != null) {
					this.loomProvider.getLoom().getMods().register(current.getName() + "_testmod")
						.configure(settings -> settings.sourceSet(ext.getSourceSets().getByName("testmod")));
				}
			}
		});
	}

	public void modules(Action<Modules> action) {
		Modules modules = new Modules(false);
		action.execute(modules);
		modules.apply(this.project.getDependencies());
	}

	@Override
	public void collectSubprojectClasspaths() {
		JavaPluginExtension javaExtension = this.project.getExtensions().getByType(JavaPluginExtension.class);
		SourceSet mainSourceSet = javaExtension.getSourceSets().getByName("main");
		this.project.afterEvaluate(ignored -> this.project.subprojects(subproject -> {
			SourceSet subProjectMainSourceSet = subproject.getExtensions().getByType(JavaPluginExtension.class).getSourceSets().getByName("main");
			mainSourceSet.setCompileClasspath(mainSourceSet.getCompileClasspath().plus(subProjectMainSourceSet.getCompileClasspath()));
			mainSourceSet.setRuntimeClasspath(mainSourceSet.getRuntimeClasspath().plus(subProjectMainSourceSet.getRuntimeClasspath()));
		}));
	}
}
