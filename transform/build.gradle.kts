plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)

    application
}

dependencies {
    implementation(libs.bundles.springBootStarter)
    implementation(libs.bundles.springCloudStream) {
        exclude(group = "org.apache.kafka", module = "kafka-streams")
        exclude(group = "org.apache.kafka", module = "kafka-clients")
    }
    implementation(libs.kafkaStreams)
    implementation(libs.kafkaClients)

    implementation(libs.bundles.logging)

    implementation(project(":kafkaSchemas"))

    testImplementation(libs.bundles.kotest)
}

application {
    mainClass = "de.fkopf.eventbased.transform.AppKt"
}
