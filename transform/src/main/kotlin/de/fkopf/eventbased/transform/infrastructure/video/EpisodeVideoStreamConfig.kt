package de.fkopf.eventbased.transform.infrastructure.video

import de.fkopf.eventbased.events.EventDeleted
import de.fkopf.eventbased.events.EventEpisodeCreated
import de.fkopf.eventbased.events.EventEpisodeVideoDeleted
import de.fkopf.eventbased.events.EventEpisodeVideoEncodingJobStarted
import de.fkopf.eventbased.transform.domain.videosource.VideoSource
import de.fkopf.eventbased.transform.infrastructure.getGrouped
import de.fkopf.eventbased.transform.infrastructure.getStore
import de.fkopf.eventbased.transform.usecases.video.DeduplicateEpisodeUpdatesUseCase
import de.fkopf.eventbased.transform.usecases.video.DeleteEpisodeUseCase
import de.fkopf.eventbased.transform.usecases.video.EncodeVideoSourceUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Function as JavaFunction

private const val UNKNOWN_EVENT_TYPE_MSG = "Unknown event type"
private val logger = KotlinLogging.logger {}

@Configuration
open class EpisodeVideoStreamConfig {

    @Bean
    open fun episodeTableProcess(
        @Qualifier("recordNameStrategySerde") valueSerde: Serde<SpecificRecord>
    ): JavaFunction<KStream<String, SpecificRecord>, KTable<String, SpecificRecord>> {
        val materializedStore = getStore("video-store", valueSerde)
        val materializedGroupBy = getGrouped("video-grouped", valueSerde)

        return JavaFunction { input ->
            input.filter { _, value ->
                listOf(
                    EventEpisodeCreated::class.java,
                    EventDeleted::class.java
                ).contains(value::class.java)
            }.groupBy({ _, value ->
                when (value) {
                    is EventEpisodeCreated -> value.data.id
                    is EventDeleted -> value.id
                    else -> throw IllegalArgumentException(UNKNOWN_EVENT_TYPE_MSG)
                }
            }, materializedGroupBy).reduce({ value, _ -> value }, materializedStore)
        }
    }

    @Bean
    open fun triggerEncoding(
        encodeVideoSourceUseCase: EncodeVideoSourceUseCase,
        deduplicateEpisodeUpdatesUseCase: DeduplicateEpisodeUpdatesUseCase,
        deleteEpisodeUseCase: DeleteEpisodeUseCase
    ): JavaFunction<KTable<String, SpecificRecord>, KStream<String, SpecificRecord>> {
        return JavaFunction { input ->
            input.toStream().flatMap { _, value ->
                val id = when (value) {
                    is EventEpisodeCreated -> value.data.id
                    is EventDeleted -> value.id
                    else -> throw IllegalArgumentException(UNKNOWN_EVENT_TYPE_MSG)
                }

                val isDeletion = value is EventDeleted

                if (isDeletion && !deleteEpisodeUseCase.hasEpisode(id)
                    || !isDeletion && deduplicateEpisodeUpdatesUseCase.isUpdateDuplicated(id)
                ) {
                    return@flatMap emptyList<KeyValue<String, SpecificRecord>>()
                }

                logger.info { "Processing episode with id $id, event ${value::class.java.simpleName}" }

                val events = if (isDeletion) {
                    val episode = deleteEpisodeUseCase.deleteEpisode(id)!!

                    episode.sources.map { videoSource ->
                        KeyValue(
                            "EventEpisodeVideoDeleted_${videoSource.id}", deleteSource(
                                connectedEpisodeId = episode.id,
                                videoSource = videoSource
                            )
                        )
                    }
                } else {
                    val job = encodeVideoSourceUseCase.encodeVideoSource(id)

                    listOf(
                        KeyValue(
                            "EventEpisodeVideoEncodingJobStarted_${job.id}", encodingJob(
                                connectedEpisodeId = id,
                                encodingJobId = job.id
                            )
                        )
                    )
                }

                events
            }
        }
    }

    private fun deleteSource(connectedEpisodeId: String, videoSource: VideoSource): EventEpisodeVideoDeleted =
        EventEpisodeVideoDeleted.newBuilder().apply {
            id = videoSource.id
            episodeId = connectedEpisodeId
            source = "transform"
        }.build()

    private fun encodingJob(connectedEpisodeId: String, encodingJobId: String): EventEpisodeVideoEncodingJobStarted =
        EventEpisodeVideoEncodingJobStarted.newBuilder().apply {
            id = encodingJobId
            episodeId = connectedEpisodeId
            source = "transform"
        }.build()
}
