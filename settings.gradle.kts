dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()

        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
    }
}

buildscript {
    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include(":ingest")
include(":query")
include(":transform")
include(":kafkaSchemas")

rootProject.name = "Event Based Services"

