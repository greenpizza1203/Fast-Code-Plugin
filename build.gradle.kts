// First, apply the publishing plugin
plugins {
    id("com.gradle.plugin-publish") version "0.12.0"
    `java-gradle-plugin`
    `kotlin-dsl`

    // Apply other plugins here, e.g. the kotlin plugin for a plugin written in Kotlin
    // or the groovy plugin if the plugin uses Groovy
}

// If your plugin has any external java dependencies, Gradle will attempt to
// download them from JCenter for anyone using the plugins DSL
// so you should probably use JCenter for dependency resolution in your own
// project.
repositories {
    jcenter()
}

dependencies {
    implementation("com.android.tools:r8:2.0.88")
    implementation("io.github.classgraph:classgraph:4.8.78")
}
// Unless overridden in the pluginBundle config DSL, the project version will
// be used as your plugin version when publishing
version = "1.0"
group = "org.moeftc"

// Use java-gradle-plugin to generate plugin descriptors and specify plugin ids
gradlePlugin {
    plugins {
        create("fastCodePlugin") {
            id = "org.moeftc.fastcode"
            implementationClass = "org.moeftc.FastCodePlugin"
        }
    }
}

// The configuration example below shows the minimum required properties
// configured to publish your plugin to the plugin portal
pluginBundle {
    website = "https://www.moeftc.org/"
    vcsUrl = "https://github.com/gradle/gradle"
    description = "Install OpModes without having to restart your FTC (First Tech Challenge) Robot Controller"
    tags = listOf("First Tech Challenge", "FTC", "FastCode")

    (plugins) {
        greetingsPlugin {
            // id is captured from java-gradle-plugin configuration
            displayName = 'Gradle Greeting plugin'
        }
    }
}