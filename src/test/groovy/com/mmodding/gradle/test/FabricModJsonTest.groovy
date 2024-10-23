package com.mmodding.gradle.test

import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

import java.nio.file.Files
import java.nio.file.Path;

class FabricModJsonTest extends Specification {

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
					gradlePluginPortal()
				}
			}
		"""
		buildFile = new File(this.testProjectDir, "build.gradle.kts")
		buildFile << """
			plugins {
				id("fabric-loom").version("1.8-SNAPSHOT")
				id("com.mmodding.gradle").version("0.0.14-alpha")
			}

			version = "0.0.1-test"

			mmodding {
				configureFabricModJson {
					name = "Test Mod"
					namespace = "test_mod"
					description = "Test Description"
					addAuthor("Test Author")
					addContributor("Test Contributor")
					withContact {
						homepage = "https://github.com/MModding/mmodding-gradle"
						sources = "https://github.com/MModding/mmodding-gradle"
						issues = "https://github.com/MModding/mmodding-gradle/issues"
					}
					withDependencies {
						javaVersion = ">=17"
						minecraftVersion = "~1.20.4"
						fabricLoaderVersion = ">=0.15.11-"
						fabricApiVersion = "0.97.1+1.20.4"
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
				mappings("net.fabricmc:yarn:1.20.4+build.3:v2")
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
			.withArguments("generateFmj")
			.build()

		then:
		Path generatedFmjPath = testProjectDir.toPath().resolve("build/generated/generated_resources/fabric.mod.json")
		if (Files.exists(generatedFmjPath)) {
			String generatedFmj = Files.readString(generatedFmjPath);
			if (generatedFmj != "") {
				println(generatedFmj)
			}
			else {
				throw new IllegalStateException("Generated FMJ is empty")
			}
		}
		else {
			throw new IllegalStateException("Generated FMJ does not exist")
		}
	}
}
