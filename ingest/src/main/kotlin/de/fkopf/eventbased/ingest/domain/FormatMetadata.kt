package de.fkopf.eventbased.ingest.domain

data class FormatMetadata(
    val id: String,
    val title: String,
    val description: String?,
    val imdbId: String?,
    val genreIds: List<String>,
)
