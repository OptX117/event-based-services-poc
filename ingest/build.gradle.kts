plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    alias(libs.plugins.kotlinPluginSerialization)

    // Apply the Application plugin to add support for building an executable JVM application.
    application
}

dependencies {
    implementation(libs.bundles.springBootStarter)
    implementation(libs.bundles.kafka)

    implementation(libs.bundles.faker)  {
        exclude(group = libs.snakeyaml.get().group, module = libs.snakeyaml.get().name)
    }

    implementation(libs.bundles.logging)

    implementation(project(":kafkaSchemas"))

    testImplementation(libs.bundles.kotest)
}

application {
    mainClass = "de.fkopf.eventbased.ingest.AppKt"
}


