package de.fkopf.eventbased.transform.infrastructure.video

import de.fkopf.eventbased.transform.domain.videosource.Episode
import org.springframework.stereotype.Service

@Service
class EpisodeRepository {
    val savedEpisode = mutableMapOf<String, Episode>()

    fun saveEpisode(episode: Episode) {
        savedEpisode[episode.id] = episode
    }

    fun getEpisode(id: String) = savedEpisode[id] ?: throw IllegalArgumentException("Episode with id $id not found")

    fun deleteEpisode(id: String) = savedEpisode.remove(id)

    fun hasEpisode(id: String) = savedEpisode.containsKey(id)
}
