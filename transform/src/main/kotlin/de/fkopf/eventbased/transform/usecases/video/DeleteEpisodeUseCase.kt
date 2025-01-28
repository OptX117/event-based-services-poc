package de.fkopf.eventbased.transform.usecases.video

import de.fkopf.eventbased.transform.infrastructure.video.EpisodeRepository
import org.springframework.stereotype.Service

@Service
class DeleteEpisodeUseCase(
    private val episodeRepository: EpisodeRepository,
) {
    fun hasEpisode(id: String) = episodeRepository.hasEpisode(id)

    fun deleteEpisode(id: String) = episodeRepository.deleteEpisode(id)

}
