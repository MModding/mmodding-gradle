# MModding Gradle

## Overview

This project is basically a continuation of a discontinued project named yumi-gradle-mc-weaving-loom that was made by
LambdAurora. This old project is now a part of MModding Gradle, so it is still important to credit people for
their stuff.

## Adding it to your project

In your `setings.gradle`:

```groovy
pluginManagement {
	resolutionStrategy {
		eachPlugin {
			if (requested.id.toString() == "com.mmodding.gradle") {
				useModule("com.mmodding:mmodding-gradle:${requested.version}")
			}
		}
	}
	repositories {
		// ...
		maven {
			name "JitPack"
			url "https://jitpack.io"
		}
		maven {
			name "QuiltMC"
			url "https://maven.quiltmc.org/repository/release"
		}
		gradlePluginPortal()
	}
}
```

***Why is the QuiltMC maven needed??? Do I need to be on QuiltMC to use this plugin???***
<br>
No, you do not need to use the QuiltMC Toolchain to use this plugin. However, this plugin uses the
[Quilt Parsers](https://github.com/QuiltMC/quilt-parsers) library to handle JSON writing, which is only
a standalone Java Library. That means it is not required at runtime for Minecraft (since it is only used
for your gradle project management), so you do not need to have it as a dependency when playing the game.

In your `libs.versions.toml`, inside the `plugins` category:

```toml
[plugins]
#...
mmodding_gradle = { id = "com.mmodding.gradle", version = "latest_version_avalaible" }
# ...
```

In your `build.gradle`:

```groovy
plugins {
    alias libs.plugins.mmodding.gradle
}
```

## License

MModding Gradle is licensed under the [PolyForm Shield 1.0.0 License](LICENSE.md).
