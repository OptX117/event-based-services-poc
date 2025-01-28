package de.fkopf.eventbased.ingest.usecases

import de.fkopf.eventbased.ingest.infrastructure.producer.EpisodeProducer
import de.fkopf.eventbased.ingest.infrastructure.producer.FormatProducer
import de.fkopf.eventbased.ingest.infrastructure.producer.SeasonProducer
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class GenerateDataUseCase(
    private val generateFormatsUseCase: GenerateFormatsUseCase,
    private val formatProducer: FormatProducer,
    private val generateSeasonsUseCase: GenerateSeasonsUseCase,
    private val seasonProducer: SeasonProducer,
    private val generateEpisodesUseCase: GenerateEpisodesUseCase,
    private val episodeProducer: EpisodeProducer
) {
    fun generateData() {
        logger.info { "Generating data" }

        val formats = generateFormatsUseCase.generateFormats()

        val seasons = formats.flatMap { format ->
            generateSeasonsUseCase.generateSeasons(format)
        }

        val episodes = seasons.flatMap { season ->
            generateEpisodesUseCase.generateEpisodes(season)
        }

        logger.info { "Generated ${formats.size} formats, ${seasons.size} seasons, ${episodes.size} episodes" }

        formatProducer.sendFormats(formats)
        seasonProducer.sendSeasons(seasons)
        episodeProducer.sendEpisodes(episodes)
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onApplicationReady() {
        this.generateData()
    }
}

