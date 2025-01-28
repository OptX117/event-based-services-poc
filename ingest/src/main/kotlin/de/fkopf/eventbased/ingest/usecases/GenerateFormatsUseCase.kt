package de.fkopf.eventbased.ingest.usecases

import com.github.javafaker.Faker
import de.fkopf.eventbased.ingest.config.Genres.genres
import de.fkopf.eventbased.ingest.domain.FormatMetadata
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GenerateFormatsUseCase (
    private val faker: Faker
){
    fun generateFormats(): List<FormatMetadata> {
        return (2..(2..10).random()).map { generateFormat() }
    }

    private fun generateFormat(): FormatMetadata {
        val formatId = UUID.randomUUID().toString()

        return FormatMetadata(
            id = formatId,
            title = faker.harryPotter().character(),
            imdbId = faker.number().digits(7),
            description = faker.harryPotter().quote(),
            genreIds = randomGenreIds()
        )
    }

    private fun randomGenreIds(): List<String> {
        return 0.rangeUntil((0..3).random()).map { genres.random().id }
    }
}

