package de.fkopf.eventbased.transform.domain

data class CombinedGenre(
    val id: String,
    val name: String,
    val imdbId: String?,
    val genreIds: List<String>
)
