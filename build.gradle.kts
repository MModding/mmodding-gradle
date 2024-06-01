plugins {
	id("dev.yumi.gradle.licenser").version("1.1.+")
	id("com.gradle.plugin-publish").version("1.2.0")
	id("maven-publish")
	id("signing")
}

group = "dev.yumi"
version = "1.0.0-alpha.1"
val javaVersion = 17

repositories {
	mavenCentral()
	maven {
		name = "FabricMC"
		url = uri("https://maven.fabricmc.net/")
	}
	maven {
		name = "QuiltMC"
		url = uri("https://maven.quiltmc.org/repository/releases/")
	}
}

// Add a source set for the functional test suite.
val functionalTest: SourceSet by sourceSets.creating

dependencies {
	compileOnly(libs.jetbrains.annotations)
	compileOnly(libs.loom.fabric)
	implementation(libs.gson)
	// Use JUnit Jupiter for testing.
	testImplementation(libs.junit.jupiter)
	testRuntimeOnly(libs.junit.launcher)
}

gradlePlugin {
	website = "https://github.com/YumiProject/yumi-gradle-mc-weaving-loom"
	vcsUrl = "https://github.com/YumiProject/yumi-gradle-mc-weaving-loom"

	// Define the plugin.
	plugins {
		create("yumi_gradle_mc_weaving_loom") {
			id = "dev.yumi.gradle.mc.weaving.loom"
			displayName = "Yumi Gradle MC Weaving Loom"
			description = ""
			tags = listOf("minecraft", "fabric-loom")
			implementationClass = "dev.yumi.gradle.mc.weaving.loom.YumiWeavingLoomGradlePlugin"
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
	from("LICENSE") {
		rename { "${it}_${base.archivesName.get()}" }
	}
}

license {
	// rule(file("codeformat/HEADER"))
	// exclude("scenarios/**")
}

signing {
	val signingKeyId: String? by project
	val signingKey: String? by project
	val signingPassword: String? by project
	isRequired = signingKeyId != null && signingKey != null && signingPassword != null
	useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
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
	systemProperty("yumi.gradle.licenser.debug", System.getProperty("yumi.gradle.licenser.debug"))

	testLogging {
		events("passed")
	}
}

publishing {
	repositories {
		mavenLocal()
	}
}
