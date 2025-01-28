package de.fkopf.eventbased.ingest.infrastructure.producer

import de.fkopf.eventbased.events.EventRelated
import de.fkopf.eventbased.events.EventSeasonCreated
import de.fkopf.eventbased.ingest.domain.SeasonMetadata
import de.fkopf.eventbased.season.Season
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class SeasonProducer(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>
) {

    fun sendSeasons(seasons: List<SeasonMetadata>) {
        seasons.forEach { season ->
            logger.info { "Sending season: $season" }
            kafkaTemplate.send("events", "EventSeasonCreated_${season.id}", EventSeasonCreated.newBuilder().apply {
                source = "ingest"
                data = season.toMessage()
            }.build()).get(5L, java.util.concurrent.TimeUnit.SECONDS)

            kafkaTemplate.send("events", "EventRelated_${season.id}", season.toRelatedMessage())
                .get(5L, java.util.concurrent.TimeUnit.SECONDS)
        }
    }

    private fun SeasonMetadata.toMessage() = Season.newBuilder().apply {
        id = this@toMessage.id
        ordinal = this@toMessage.ordinal
        formatId = this@toMessage.formatId
        title = this@toMessage.title
        description = this@toMessage.description
        imdbId = this@toMessage.imdbId
        genreIds = this@toMessage.genreIds
    }.build()

    private fun SeasonMetadata.toRelatedMessage() = EventRelated.newBuilder().apply {
        source = "ingest"
        id = this@toRelatedMessage.id
        related = listOfNotNull(
            this@toRelatedMessage.formatId
        ) + this@toRelatedMessage.genreIds
    }.build()
}
