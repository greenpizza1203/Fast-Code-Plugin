// First, apply the publishing plugin
plugins {
    id("com.gradle.plugin-publish") version "0.12.0"
    `java-gradle-plugin`
    `maven-publish`
}

val explode by configurations.creating {
    configurations.compileOnly.get().extendsFrom(this)
}
repositories {
    google()
    jcenter()
    maven(url = "https://jitpack.io")
}

dependencies {
    explode("com.android.tools:r8:2.0.88")
    explode("io.github.classgraph:classgraph:4.8.87")
}
version = "1.1"
group = "org.moeftc"

gradlePlugin {
    plugins {
        create("fastCode") {
            id = "org.moeftc.fastcode"
            implementationClass = "org.moeftc.FastCodePlugin"
        }
    }
}

pluginBundle {
    website = "https://www.moeftc.org/"
    vcsUrl = "https://github.com/greenpizza1203/Fast-Code-Plugin"
    description = "Install OpModes without having to restart your FTC (First Tech Challenge) Robot Controller"
    tags = listOf("First Tech Challenge", "FTC", "FastCode")

    (plugins) {
        "fastCode" {
            displayName = "FTC Fast Code Plugin"
        }
    }
}

tasks {
    jar {
        val explodedFiles = explode.resolve().map { zipTree(it) }
        from(explodedFiles)
    }
}
