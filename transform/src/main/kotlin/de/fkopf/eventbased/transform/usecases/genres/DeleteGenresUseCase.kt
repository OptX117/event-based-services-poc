package de.fkopf.eventbased.transform.usecases.genres

import de.fkopf.eventbased.transform.infrastructure.genres.GenreRepository
import org.springframework.stereotype.Service

@Service
class DeleteGenresUseCase(
    private val genreRepository: GenreRepository
) {
    fun deleteGenre(id: String) {
        val connectedGenres = genreRepository.findGenresWithIdInGenreIds(id)

        connectedGenres.forEach {
            genreRepository.deleteGenre(it.id)
            genreRepository.deleteSeenGenreId(it.id)
        }

        genreRepository.deleteSeenGenreId(id)
    }
}
