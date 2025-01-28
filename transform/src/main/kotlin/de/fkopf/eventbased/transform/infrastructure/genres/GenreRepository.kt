package de.fkopf.eventbased.transform.infrastructure.genres

import de.fkopf.eventbased.transform.domain.CombinedGenre
import org.springframework.stereotype.Service

@Service
class GenreRepository {
    val savedGenres = mutableSetOf<CombinedGenre>()
    val seenGenreIds = mutableMapOf<String, Int>()

    fun saveSeenGenreId(id: String, hash: Int) = seenGenreIds.put(id, hash)

    fun hasSeenGenreId(id: String) = seenGenreIds.contains(id)

    fun getSeenGenreId(id: String) = seenGenreIds[id]

    fun deleteSeenGenreId(id: String) = seenGenreIds.remove(id)

    fun saveGenre(genre: CombinedGenre) = savedGenres.add(genre)

    fun deleteGenre(id: String) = savedGenres.removeIf { it.id == id }

    fun findGenresWithIdInGenreIds(id: String) = savedGenres.filter { it.genreIds.contains(id) }
}
