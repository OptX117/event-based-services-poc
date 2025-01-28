package de.fkopf.eventbased.ingest.config

import de.fkopf.eventbased.ingest.domain.GenreMetadata


object Genres {
    val genres = listOf(
        GenreMetadata(id = "1", name = "Action", imdbId = "tt0000001"),
        GenreMetadata(id = "2", name = "Comedy", imdbId = "tt0000002"),
        GenreMetadata(id = "3", name = "Drama", imdbId = "tt0000003"),
        GenreMetadata(id = "4", name = "Fantasy", imdbId = "tt0000004"),
        GenreMetadata(id = "5", name = "Horror", imdbId = "tt0000005"),
        GenreMetadata(id = "6", name = "Mystery", imdbId = "tt0000006"),
        GenreMetadata(id = "7", name = "Romance", imdbId = "tt0000007"),
        GenreMetadata(id = "8", name = "Thriller", imdbId = "tt0000008"),
        GenreMetadata(id = "9", name = "Western", imdbId = "tt0000009"),
        GenreMetadata(id = "10", name = "Science Fiction", imdbId = "tt0000010")
    )
}
