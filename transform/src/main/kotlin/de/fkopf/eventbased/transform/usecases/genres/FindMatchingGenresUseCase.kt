package de.fkopf.eventbased.transform.usecases.genres

import de.fkopf.eventbased.transform.config.CombinedGenres
import de.fkopf.eventbased.transform.domain.CombinedGenre
import de.fkopf.eventbased.transform.infrastructure.genres.GenreRepository
import org.springframework.stereotype.Service

@Service
class FindMatchingGenresUseCase(
    private val genreRepository: GenreRepository
) {

    fun isSeenGenreId(id: String) = genreRepository.hasSeenGenreId(id)

    fun findMatchingGenres(id: String): List<CombinedGenre> {
        val combinedGenres = CombinedGenres.genres.filter {
            it.genreIds.contains(id) && it.genreIds.all { genreId ->
                genreRepository.hasSeenGenreId(
                    genreId
                )
            }
        }

        combinedGenres.forEach { combinedGenre -> genreRepository.saveGenre(combinedGenre) }

        return combinedGenres
    }
}
