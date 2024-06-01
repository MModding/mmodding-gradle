plugins {
	id("fabric-loom").version(libs.versions.loom.get())
	id("dev.yumi.gradle.mc.weaving.loom").version("1.0.0-alpha.1")
}

version = "yippee"

weavingLoom {
	addFabricModManifest {
		namespace = "test"
		description = "Test description."
	}
}

dependencies {
	minecraft("com.mojang:minecraft:1.20.5-pre1")
	mappings("net.fabricmc:yarn:1.20.5-pre1+build.5")

	weavingLoom.withFabricManifest(include("dev.yumi.commons:yumi-commons-core:1.0.0-alpha.1")) {
		namespace = "yumi-commons-core";
		name = "Yumi Commons: Core"
		description = "Test"
		withContact {
			email = "email@yumi.dev"
		}
		addAuthor("LambdAurora") {
			withContact {
				email = "email@lambdaurora.dev"
			}
		}
		addAuthor("Example")
	}
}
