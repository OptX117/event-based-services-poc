package de.fkopf.eventbased.ingest.infrastructure.producer

import de.fkopf.eventbased.events.EventGenreCreated
import de.fkopf.eventbased.genre.Genre
import de.fkopf.eventbased.ingest.config.Genres.genres
import de.fkopf.eventbased.ingest.domain.GenreMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class GenreProducer(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>
) {

    fun sendGenres() {
        genres.forEach { genre ->
            logger.info { "Sending genre: $genre" }
            kafkaTemplate.send("events", "EventGenreCreated_${genre.id}", EventGenreCreated.newBuilder().apply {
                source = "ingest"
                data = genre.toMessage()
            }.build()).get(5L, java.util.concurrent.TimeUnit.SECONDS)
        }

    }

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        this.sendGenres()
    }

    private fun GenreMetadata.toMessage() = Genre.newBuilder().apply {
        id = this@toMessage.id
        name = this@toMessage.name
        imdbId = this@toMessage.imdbId
    }.build()
}
