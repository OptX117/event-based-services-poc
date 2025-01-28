package de.fkopf.eventbased.transform.usecases.video

import de.fkopf.eventbased.transform.domain.videosource.AudioTrack
import de.fkopf.eventbased.transform.domain.videosource.EncodingJob
import de.fkopf.eventbased.transform.domain.videosource.VideoSource
import de.fkopf.eventbased.transform.infrastructure.video.EncodingJobRepository
import de.fkopf.eventbased.transform.infrastructure.video.VideoSourceKafkaSupplier
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.net.URI
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class EncodeVideoSourceUseCase(
    private val encodingJobRepository: EncodingJobRepository,
    private val taskScheduler: TaskScheduler,
    private val videoSourceKafkaSupplier: VideoSourceKafkaSupplier
) {
    fun encodeVideoSource(episodeId: String): EncodingJob {
        val encodingJob = EncodingJob(
            id = UUID.randomUUID().toString(),
            fileUri = URI("https://example.com/$episodeId.mp4")
        )

        encodingJobRepository.saveEncodingJob(episodeId, encodingJob)

        // Simulate encoding process

        taskScheduler.schedule(
            {
                val numberOfNewSources = (1..4).random()

                // Generate new sources, stand-in for encoding process
                val sources = (1..numberOfNewSources).map {
                    val episodeId = episodeId
                    val videoId = UUID.randomUUID().toString()

                    VideoSource(
                        id = videoId,
                        name = "Source $episodeId $videoId",
                        uri = URI("https://example.com/$episodeId/$videoId.mp4"),
                        audioTracks = listOf(
                            AudioTrack(
                                id = videoId + "_0",
                                language = "en",
                                codec = "aac",
                                bitrate = 256,
                                channels = "5.1"
                            ),
                            AudioTrack(
                                id = videoId + "_1",
                                language = "de",
                                codec = "aac",
                                bitrate = 256,
                                channels = "5.1"
                            )
                        ),
                        duration = Duration.ofMinutes(30),
                        resolution = Pair(1920, 1080),
                        bitrate = 5000,
                        codec = "h265"
                    )
                }

                videoSourceKafkaSupplier.sendVideoSources(episodeId, sources)
            },
            Instant.now().plus(Duration.ofSeconds(10))
        )

        return encodingJob
    }
}
