plugins {
	id("com.gradle.plugin-publish").version("1.2.0")
	id("maven-publish")
}

group = "com.mmodding"
version = "0.0.2-alpha"
val javaVersion = 17

repositories {
	mavenCentral()
	maven {
		name = "FabricMC"
		url = uri("https://maven.fabricmc.net/")
	}
	maven {
		name = "QuiltMC"
		url = uri("https://maven.quiltmc.org/repository/release/")
	}
}

// Add a source set for the functional test suite.
val functionalTest: SourceSet by sourceSets.creating

dependencies {
	compileOnly(libs.jetbrains.annotations)

	compileOnly(libs.loom.fabric)

	implementation(libs.quilt.parsers.json)

	// Use JUnit Jupiter for testing.
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly(libs.junit.launcher)
}

gradlePlugin {
	website = "https://mmodding.com"
	vcsUrl = "https://github.com/MModding/mmodding-gradle"

	// Define the plugin.
	plugins {
		register("mmodding_gradle") {
			id = "com.mmodding.gradle"
			displayName = "MModding Gradle"
			description = "List of Gradle Tools to Help in Minecraft Mod Development"
			tags = listOf("minecraft", "fabric-loom", "quilt-loom")
			implementationClass = "com.mmodding.gradle.MModdingGradlePlugin"
		}
	}

	testSourceSets(functionalTest)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(javaVersion))
	}

	withSourcesJar()
	withJavadocJar()

	testResultsDir.set(layout.buildDirectory.dir("junit-xml"))
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
	options.isDeprecation = true
	options.release.set(javaVersion)
}

tasks.withType<Javadoc>().configureEach {
	options {
		this as StandardJavadocDocletOptions

		addStringOption("Xdoclint:all,-missing", "-quiet")
	}
}

tasks.jar {
	from("LICENSE.md") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

configurations["functionalTestImplementation"].extendsFrom(configurations.testImplementation.get())

val functionalTestTask = tasks.register<Test>("functionalTest") {
	group = "verification"
	testClassesDirs = functionalTest.output.classesDirs
	classpath = functionalTest.runtimeClasspath
}

tasks.check {
	// Run the functional tests as part of `check`.
	dependsOn(functionalTestTask)
}

tasks.withType<Test>().configureEach {
	// Using JUnitPlatform for running tests
	useJUnitPlatform()

	testLogging {
		events("passed")
	}
}

publishing {
	repositories {
		mavenLocal()
	}
}
