package de.fkopf.eventbased.ingest.infrastructure.producer

import de.fkopf.eventbased.events.EventFormatCreated
import de.fkopf.eventbased.events.EventRelated
import de.fkopf.eventbased.format.Format
import de.fkopf.eventbased.ingest.domain.EpisodeMetadata
import de.fkopf.eventbased.ingest.domain.FormatMetadata
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class FormatProducer(
    private val kafkaTemplate: KafkaTemplate<String, SpecificRecord>
) {

    fun sendFormats(formats: List<FormatMetadata>) {
        formats.forEach { format ->
            logger.info { "Sending format: $format" }
            kafkaTemplate.send("events", "EventFormatCreated_${format.id}", EventFormatCreated.newBuilder().apply {
                source = "ingest"
                data = format.toMessage()
            }.build()).get(5L, java.util.concurrent.TimeUnit.SECONDS)

            kafkaTemplate.send("events", "EventRelated_${format.id}", format.toRelatedMessage())
                .get(5L, java.util.concurrent.TimeUnit.SECONDS)
        }
    }

    private fun FormatMetadata.toMessage() = Format.newBuilder().apply {
        id = this@toMessage.id
        title = this@toMessage.title
        description = this@toMessage.description
        imdbId = this@toMessage.imdbId
        genreIds = this@toMessage.genreIds
    }.build()

    private fun FormatMetadata.toRelatedMessage() = EventRelated.newBuilder().apply {
        source = "ingest"
        id = this@toRelatedMessage.id
        related = this@toRelatedMessage.genreIds
    }.build()
}
