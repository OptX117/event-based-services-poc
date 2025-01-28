package de.fkopf.eventbased.transform.domain.videosource

data class Episode(
    val id: String,
    val title: String,
    val sources: List<VideoSource>,
)
