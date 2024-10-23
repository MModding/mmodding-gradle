package com.mmodding.gradle.test

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path;

class QuiltModJsonTest extends Specification {

	@TempDir
	File testProjectDir

	File settingsFile
	File buildFile

	def setup() {
		settingsFile = new File(this.testProjectDir, "settings.gradle.kts")
		settingsFile << """
			pluginManagement {
				repositories {
					maven {
						name = "Fabric"
						url = uri("https://maven.fabricmc.net/")
					}
					maven {
						name = "Quilt"
						url = uri("https://maven.quiltmc.org/repository/release")
					}
					gradlePluginPortal()
				}
			}
		"""
		buildFile = new File(this.testProjectDir, "build.gradle.kts")
		buildFile << """
			plugins {
				id("org.quiltmc.loom").version("1.8.+")
				id("com.mmodding.gradle").version("0.0.11-alpha")
			}

			version = "0.0.1-test"

			mmodding {
				configureQuiltModJson {
					name = "Test Mod"
					namespace = "test_mod"
					group = "com.mmodding.test"
					description = "Test Description"
					addContributor("Test Owner", "Owner")
					withContact {
						homepage = "https://github.com/MModding/mmodding-gradle"
						sources = "https://github.com/MModding/mmodding-gradle"
						issues = "https://github.com/MModding/mmodding-gradle/issues"
					}
					withDependencies {
						javaVersion = ">=17"
						minecraftVersion = "~1.20.4"
						quiltLoaderVersion = ">=0.25.0-"
						quiltedFabricApiVersion = ">=1.0.0-"
					}
					withProvider {
						provide("test-mod")
					}
					withParent("gradle_tests")
					withCustom {
						withBlock("modmenu") {
							withArray("badges") {
								addUnique("library")
							}
						}
					}
				}
			}

			dependencies {
				minecraft("com.mojang:minecraft:1.20.4")
				mappings("org.quiltmc:quilt-mappings:1.20.4+build.3:intermediary-v2")
			}

			java {
				withSourcesJar()
			}
		"""
	}

	def "can successfully gather data and nest it"() {
		when:
		GradleRunner.create()
			.withProjectDir(testProjectDir)
			.withPluginClasspath()
			.withArguments("generateQMJ")
			.build()

		then:
		Path generatedQmjPath = testProjectDir.toPath().resolve("build/generated/generated_resources/quilt.mod.json")
		if (Files.exists(generatedQmjPath)) {
			String generatedQmj = Files.readString(generatedQmjPath);
			if (generatedQmj != "") {
				println(generatedQmj)
			}
			else {
				throw new IllegalStateException("Generated QMJ is empty")
			}
		}
		else {
			throw new IllegalStateException("Generated QMJ does not exist")
		}
	}
}
