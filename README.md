# MModding Gradle

## Overview

This project is basically a continuation of a discontinued project named yumi-gradle-mc-weaving-loom that was made by
LambdAurora. This old project is now only a part of MModding Gradle, but it is still important to credit people for
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
        gradlePluginPortal()
    }
}
```

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
