package de.fkopf.eventbased.ingest.usecases

import com.github.javafaker.Faker
import de.fkopf.eventbased.ingest.config.Genres.genres
import de.fkopf.eventbased.ingest.domain.FormatMetadata
import de.fkopf.eventbased.ingest.domain.SeasonMetadata
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GenerateSeasonsUseCase(
    private val faker: Faker
) {

    fun generateSeasons(format: FormatMetadata): List<SeasonMetadata> {
        return (1..(0..5).random()).map { ordinal -> generateSeason(format, ordinal) }
    }

    private fun generateSeason(format: FormatMetadata, ordinal: Int): SeasonMetadata {
        val seasonId = UUID.randomUUID().toString()

        return SeasonMetadata(
            id = seasonId,
            ordinal = ordinal,
            title = faker.harryPotter().location(),
            description = faker.harryPotter().quote(),
            imdbId = faker.number().digits(7),
            formatId = format.id,
            genreIds = format.genreIds + randomGenreIds()
        )
    }

    private fun randomGenreIds(): List<String> {
        return if ((0..10).random() == 0) {
            listOf(genres.random().id)
        } else {
            emptyList()
        }
    }

}
