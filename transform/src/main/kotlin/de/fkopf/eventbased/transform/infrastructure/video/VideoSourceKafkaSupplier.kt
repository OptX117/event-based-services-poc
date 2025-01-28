package de.fkopf.eventbased.transform.infrastructure.video

import de.fkopf.eventbased.events.EventEpisodeVideoEncoded
import de.fkopf.eventbased.transform.domain.videosource.VideoSource
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import de.fkopf.eventbased.episode.VideoSource as VideoSourceMessage

@Service
class VideoSourceKafkaSupplier(private val streamBridge: StreamBridge) {

    fun sendVideoSources(episodeId: String, videoSources: List<VideoSource>) {
        videoSources.mapIndexed { index, videoSource ->
            encodeSource(episodeId, index, videoSource)
        }
            .map {
                MessageBuilder.withPayload(it)
                    .setHeader(KafkaHeaders.KEY, "EventEpisodeVideoEncoded_${it.data.id}")
                    .build()
            }.forEach { message ->
                streamBridge.send("encodingJobFinishedSource-out-0", message)
            }
    }

    private fun encodeSource(episodeId: String, ordinal: Int, videoSource: VideoSource): EventEpisodeVideoEncoded =
        EventEpisodeVideoEncoded.newBuilder().apply {
            data = videoSource.toVideoSourceMessage(episodeId, ordinal)
            source = "transform"
        }.build()

    private fun VideoSource.toVideoSourceMessage(
        connectedEpisodeId: String,
        sortOrdinal: Int
    ): VideoSourceMessage =
        VideoSourceMessage.newBuilder().apply {
            id = this@toVideoSourceMessage.id
            uri = this@toVideoSourceMessage.uri.toString()
            episodeId = connectedEpisodeId
            ordinal = sortOrdinal
            mediaInfo = de.fkopf.eventbased.episode.MediaInfo.newBuilder().apply {
                duration = this@toVideoSourceMessage.duration.seconds
                codec = this@toVideoSourceMessage.codec
                bitrate = this@toVideoSourceMessage.bitrate
                width = this@toVideoSourceMessage.resolution.first
                height = this@toVideoSourceMessage.resolution.second
                audioTracks = this@toVideoSourceMessage.audioTracks.map {
                    de.fkopf.eventbased.episode.AudioTrack.newBuilder().apply {
                        id = it.id
                        language = it.language
                        channels = it.channels
                        bitrate = it.bitrate
                        codec = it.codec
                    }.build()
                }
            }.build()
        }.build()
}
