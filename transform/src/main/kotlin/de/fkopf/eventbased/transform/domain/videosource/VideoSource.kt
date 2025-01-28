package de.fkopf.eventbased.transform.domain.videosource

import java.net.URI
import java.time.Duration

data class VideoSource(
    val id: String,
    val name: String,
    val uri: URI,
    val audioTracks: List<AudioTrack>,
    val duration: Duration,
    val resolution: Pair<Int, Int>,
    val bitrate: Int,
    val codec: String
)
