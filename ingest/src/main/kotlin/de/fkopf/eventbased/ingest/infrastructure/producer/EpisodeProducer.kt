package de.fkopf.eventbased.ingest.infrastructure.producer

import de.fkopf.eventbased.episode.Episode
import de.fkopf.eventbased.events.EventEpisodeCreated
import de.fkopf.eventbased.events.EventRelated
import de.fkopf.eventbased.ingest.domain.EpisodeMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class EpisodeProducer(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>
) {

    fun sendEpisodes(episodes: List<EpisodeMetadata>) {
        episodes.forEach { episode ->
            logger.info { "Sending episode: $episode" }
            kafkaTemplate.send("events", "EventEpisodeCreated_${episode.id}", EventEpisodeCreated.newBuilder().apply {
                source = "ingest"
                data = episode.toMessage()
            }.build()).get(5L, java.util.concurrent.TimeUnit.SECONDS)

            kafkaTemplate.send("events", "EventRelated_${episode.id}", episode.toRelatedMessage())
                .get(5L, java.util.concurrent.TimeUnit.SECONDS)
        }
    }

    private fun EpisodeMetadata.toMessage() = Episode.newBuilder().apply {
        id = this@toMessage.id
        ordinal = this@toMessage.ordinal
        seasonId = this@toMessage.seasonId
        formatId = this@toMessage.formatId
        title = this@toMessage.title
        description = this@toMessage.description
        imdbId = this@toMessage.imdbId
        genreIds = this@toMessage.genreIds
    }.build()

    private fun EpisodeMetadata.toRelatedMessage() = EventRelated.newBuilder().apply {
        source = "ingest"
        id = this@toRelatedMessage.id
        related = listOfNotNull(
            this@toRelatedMessage.seasonId,
            this@toRelatedMessage.formatId,
        ) + this@toRelatedMessage.genreIds
    }.build()
}
