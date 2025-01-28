package de.fkopf.eventbased.transform.usecases.video

import de.fkopf.eventbased.transform.infrastructure.video.EpisodeRepository
import org.springframework.stereotype.Service

@Service
class DeduplicateEpisodeUpdatesUseCase(
    private val episodeRepository: EpisodeRepository
) {

    fun isUpdateDuplicated(id: String) = episodeRepository.hasEpisode(id)
}
