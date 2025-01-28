package de.fkopf.eventbased.transform.domain.videosource

import java.net.URI

data class EncodingJob(
    val id: String,
    val fileUri: URI,
    val done: Boolean = false,
    val results: List<VideoSource> = emptyList()
)
