package de.fkopf.eventbased.ingest.usecases

import com.github.javafaker.Faker
import de.fkopf.eventbased.ingest.config.Genres.genres
import de.fkopf.eventbased.ingest.domain.EpisodeMetadata
import de.fkopf.eventbased.ingest.domain.SeasonMetadata
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GenerateEpisodesUseCase(
    private val faker: Faker
) {

    fun generateEpisodes(season: SeasonMetadata): List<EpisodeMetadata> {
        return (1..(0..10).random()).map { ordinal -> generateEpisode(season, ordinal) }
    }

    private fun generateEpisode(season: SeasonMetadata, ordinal: Int): EpisodeMetadata {
        val episodeId = UUID.randomUUID().toString()

        return EpisodeMetadata(
            id = episodeId,
            ordinal = ordinal,
            title = faker.team().name(),
            description = faker.team().sport(),
            imdbId = faker.number().digits(7),
            seasonId = season.id,
            formatId = season.formatId,
            genreIds = season.genreIds + randomGenres()
        )
    }

    private fun randomGenres(): List<String> {
        return if ((0..10).random() == 0) {
            listOf(genres.random().id)
        } else {
            emptyList()
        }
    }
}
