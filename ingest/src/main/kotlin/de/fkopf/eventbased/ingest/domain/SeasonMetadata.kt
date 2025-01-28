package de.fkopf.eventbased.ingest.domain

data class SeasonMetadata(
    val id: String,
    val ordinal: Int,
    val title: String,
    val description: String?,
    val imdbId: String?,
    val formatId: String,
    val genreIds: List<String>,
)
