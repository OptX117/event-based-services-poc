package de.fkopf.eventbased.transform.config

import de.fkopf.eventbased.transform.domain.CombinedGenre

object CombinedGenres {
    val genres = listOf(
        CombinedGenre(
            id = "11",
            name = "Romantic Comedy",
            imdbId = "tt0000011",
            genreIds = listOf("2", "7")
        ),
        CombinedGenre(
            id = "12",
            name = "Action Comedy",
            imdbId = "tt0000012",
            genreIds = listOf("1", "2")
        ),
        CombinedGenre(
            id = "13",
            name = "Action Thriller",
            imdbId = "tt0000013",
            genreIds = listOf("1", "8")
        ),
        CombinedGenre(
            id = "14",
            name = "Action Adventure",
            imdbId = "tt0000014",
            genreIds = listOf("1", "4")
        ),
        CombinedGenre(
            id = "15",
            name = "Science Fiction Thriller",
            imdbId = "tt0000015",
            genreIds = listOf("10", "8")
        ),
        CombinedGenre(
            id = "16",
            name = "Science Fiction Mystery",
            imdbId = "tt0000016",
            genreIds = listOf("10", "6")
        ),
        CombinedGenre(
            id = "17",
            name = "Science Fiction Adventure",
            imdbId = "tt0000017",
            genreIds = listOf("10", "4")
        ),
        CombinedGenre(
            id = "18",
            name = "Science Fiction Horror",
            imdbId = "tt0000018",
            genreIds = listOf("10", "5")
        ),
        CombinedGenre(
            id = "19",
            name = "Science Fiction Comedy",
            imdbId = "tt0000019",
            genreIds = listOf("10", "2")
        ),
        CombinedGenre(
            id = "20",
            name = "Science Fiction Action",
            imdbId = "tt0000020",
            genreIds = listOf("10", "1")
        )
    )
}
