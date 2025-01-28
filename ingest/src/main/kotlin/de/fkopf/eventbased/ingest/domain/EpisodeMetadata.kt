package de.fkopf.eventbased.ingest.domain

data class EpisodeMetadata(
    val id: String,
    val ordinal: Int,
    val title: String,
    val description: String,
    val imdbId: String?,
    val formatId: String,
    val seasonId: String?,
    val genreIds: List<String>,
)
