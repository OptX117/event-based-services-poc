package de.fkopf.eventbased.transform.domain.videosource

data class AudioTrack(
    val id: String,
    val language: String,
    val channels: String,
    val bitrate: Int,
    val codec: String
)
