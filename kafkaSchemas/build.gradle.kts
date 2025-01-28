plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")

    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)

    alias(libs.plugins.gradleAvroPlugin)
    alias(libs.plugins.kafkaSchemaRegistryPlugin)
}

dependencies {
    implementation(libs.bundles.springBootStarter)
    implementation(libs.bundles.kafka)

    testImplementation(libs.bundles.kotest)
}

val generateProtocol =
    tasks.register("generateProtocol", com.github.davidmc24.gradle.plugin.avro.GenerateAvroProtocolTask::class.java) {
        source(file("src/main/avro/schemas"))
        group = "source generation"
        description = "Generate Avro protocol files from avdl files"
        include("**/*.avdl")
        setOutputDir(file("build/generated-avro-avpr"))
    }

tasks.register("generateSchema", com.github.davidmc24.gradle.plugin.avro.GenerateAvroSchemaTask::class.java) {
    dependsOn(generateProtocol)
    group = "source generation"
    description = "Generate Avro schema files from avpr files"
    source(file("src/main/avro"))
    source(file("build/generated-avro-avpr"))
    include("**/*.avpr")
    setOutputDir(file("build/generated-avro-avsc"))
}

avro {
    setFieldVisibility(org.apache.avro.compiler.specific.SpecificCompiler.FieldVisibility.PRIVATE)
}

schemaRegistry {
    register {
        subject(
            "de.fkopf.eventbased.Season",
            "$projectDir/src/main/avro/schemas/de.fkopf.eventbased.Season.avsc",
            "AVRO"
        )
        subject(
            "de.fkopf.eventbased.Genre",
            "$projectDir/src/main/avro/schemas/de.fkopf.eventbased.Genre.avsc",
            "AVRO"
        )
        subject(
            "de.fkopf.eventbased.Format",
            "$projectDir/src/main/avro/schemas/de.fkopf.eventbased.Format.avsc",
            "AVRO"
        )
        subject(
            "de.fkopf.eventbased.Episode",
            "$projectDir/src/main/avro/schemas/de.fkopf.eventbased.Episode.avsc",
            "AVRO"
        )
        subject(
            "de.fkopf.eventbased.VideoSource",
            "$projectDir/src/main/avro/schemas/de.fkopf.eventbased.VideoSource.avsc",
            "AVRO"
        )

        /* Events */

        /* Meta */
        subject(
            "de.fkopf.eventbased.events.EventDeleted",
            "$projectDir/src/main/avro/schemas/events/meta/EventDeleted.avsc",
            "AVRO"
        )

        subject(
            "de.fkopf.eventbased.events.EventRelated",
            "$projectDir/src/main/avro/schemas/events/meta/EventRelated.avsc",
            "AVRO"
        )

        /* Episode */
        subject(
            "de.fkopf.eventbased.events.EventEpisodeCreated",
            "$projectDir/src/main/avro/schemas/events/episode/EventEpisodeCreated.avsc",
            "AVRO"
        )
            .addReference("Episode", "de.fkopf.eventbased.Episode")

        subject(
            "de.fkopf.eventbased.events.EventEpisodeUpdated",
            "$projectDir/src/main/avro/schemas/events/episode/EventEpisodeUpdated.avsc",
            "AVRO"
        )
            .addReference("Episode", "de.fkopf.eventbased.Episode")

        subject(
            "de.fkopf.eventbased.events.EventEpisodeVideoEncoded",
            "$projectDir/src/main/avro/schemas/events/episode/EventEpisodeVideoEncoded.avsc",
            "AVRO"
        )
            .addReference("VideoSource", "de.fkopf.eventbased.VideoSource")

        subject(
            "de.fkopf.eventbased.events.EventEpisodeVideoEncodingJobStarted",
            "$projectDir/src/main/avro/schemas/events/episode/EventEpisodeVideoEncodingJobStarted.avsc",
            "AVRO"
        )


        // We need a dedicated event as we need to include the episode ID for easy reference.
        subject(
            "de.fkopf.eventbased.events.EventEpisodeVideoDeleted",
            "$projectDir/src/main/avro/schemas/events/episode/EventEpisodeVideoDeleted.avsc",
            "AVRO"
        )

        /* Format */
        subject(
            "de.fkopf.eventbased.events.EventFormatCreated",
            "$projectDir/src/main/avro/schemas/events/format/EventFormatCreated.avsc",
            "AVRO"
        )
            .addReference("Format", "de.fkopf.eventbased.Format")

        subject(
            "de.fkopf.eventbased.events.EventFormatUpdated",
            "$projectDir/src/main/avro/schemas/events/format/EventFormatUpdated.avsc",
            "AVRO"
        )
            .addReference("Format", "de.fkopf.eventbased.Format")

        /* Genre */
        subject(
            "de.fkopf.eventbased.events.EventGenreCreated",
            "$projectDir/src/main/avro/schemas/events/genre/EventGenreCreated.avsc",
            "AVRO"
        )
            .addReference("Genre", "de.fkopf.eventbased.Genre")

        subject(
            "de.fkopf.eventbased.events.EventGenreUpdated",
            "$projectDir/src/main/avro/schemas/events/genre/EventGenreUpdated.avsc",
            "AVRO"
        )
            .addReference("Genre", "de.fkopf.eventbased.Genre")

        /* Season */
        subject(
            "de.fkopf.eventbased.events.EventSeasonCreated",
            "$projectDir/src/main/avro/schemas/events/season/EventSeasonCreated.avsc",
            "AVRO"
        )
            .addReference("Season", "de.fkopf.eventbased.Season")

        subject(
            "de.fkopf.eventbased.events.EventSeasonUpdated",
            "$projectDir/src/main/avro/schemas/events/season/EventSeasonUpdated.avsc",
            "AVRO"
        )
            .addReference("Season", "de.fkopf.eventbased.Season")

        subject(
            "events-value",
            "$projectDir/src/main/avro/schemas/events/de.fkopf.eventbased.events.all-events.avsc",
            "AVRO"
        )
            .addReference("EventEpisodeCreated", "de.fkopf.eventbased.events.EventEpisodeCreated")
            .addReference("EventEpisodeUpdated", "de.fkopf.eventbased.events.EventEpisodeUpdated")
            .addReference("EventEpisodeVideoEncoded", "de.fkopf.eventbased.events.EventEpisodeVideoEncoded")
            .addReference("EventEpisodeVideoEncodingJobStarted", "de.fkopf.eventbased.events.EventEpisodeVideoEncodingJobStarted")
            .addReference("EventEpisodeVideoDeleted", "de.fkopf.eventbased.events.EventEpisodeVideoDeleted")
            .addReference("EventFormatCreated", "de.fkopf.eventbased.events.EventFormatCreated")
            .addReference("EventFormatUpdated", "de.fkopf.eventbased.events.EventFormatUpdated")
            .addReference("EventGenreCreated", "de.fkopf.eventbased.events.EventGenreCreated")
            .addReference("EventGenreUpdated", "de.fkopf.eventbased.events.EventGenreUpdated")
            .addReference("EventSeasonCreated", "de.fkopf.eventbased.events.EventSeasonCreated")
            .addReference("EventSeasonUpdated", "de.fkopf.eventbased.events.EventSeasonUpdated")
            .addReference("EventDeleted", "de.fkopf.eventbased.events.EventDeleted")
            .addReference("EventRelated", "de.fkopf.eventbased.events.EventRelated")
    }
}

