package de.fkopf.eventbased.transform.infrastructure.video

import de.fkopf.eventbased.transform.domain.videosource.EncodingJob
import org.springframework.stereotype.Service

@Service
class EncodingJobRepository {
    val savedEncodingJobs = mutableMapOf<String, List<EncodingJob>>()

    fun saveEncodingJob(episodeId: String, job: EncodingJob): EncodingJob {
        savedEncodingJobs.compute(episodeId) { _, existing ->
            val list = existing ?: listOf()
            val filteredList = list.filter { it.id != job.id }

            filteredList + job
        }

        return job
    }

    fun deleteEncodingJobsByEpisode(episodeId: String) = savedEncodingJobs.remove(episodeId)

    fun getEncodingJobsByEpisode(episodeId: String) = savedEncodingJobs[episodeId] ?: emptyList()
}
