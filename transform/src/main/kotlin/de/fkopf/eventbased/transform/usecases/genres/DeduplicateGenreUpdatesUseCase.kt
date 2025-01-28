package de.fkopf.eventbased.transform.usecases.genres

import de.fkopf.eventbased.transform.domain.CombinedGenre
import de.fkopf.eventbased.transform.infrastructure.genres.GenreRepository
import org.springframework.stereotype.Service
import java.util.Objects

@Service
class DeduplicateGenreUpdatesUseCase(
    private val genreRepository: GenreRepository
) {

    fun isUpdateDuplicated(new: CombinedGenre): Boolean {
        val alreadySeen = genreRepository.hasSeenGenreId(new.id)

        val hashCode = Objects.hashCode(new)

        if (!alreadySeen) {
            genreRepository.saveSeenGenreId(new.id, hashCode)

            return false
        }

        val seen = genreRepository.getSeenGenreId(new.id)

        return seen == hashCode
    }
}
