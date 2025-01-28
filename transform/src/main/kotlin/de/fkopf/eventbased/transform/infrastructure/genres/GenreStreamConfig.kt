package de.fkopf.eventbased.transform.infrastructure.genres

import de.fkopf.eventbased.events.EventDeleted
import de.fkopf.eventbased.events.EventGenreCreated
import de.fkopf.eventbased.events.EventGenreUpdated
import de.fkopf.eventbased.genre.Genre
import de.fkopf.eventbased.transform.domain.CombinedGenre
import de.fkopf.eventbased.transform.infrastructure.getGrouped
import de.fkopf.eventbased.transform.infrastructure.getStore
import de.fkopf.eventbased.transform.usecases.genres.DeduplicateGenreUpdatesUseCase
import de.fkopf.eventbased.transform.usecases.genres.DeleteGenresUseCase
import de.fkopf.eventbased.transform.usecases.genres.FindMatchingGenresUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.KTable
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.function.Function as JavaFunction

private const val UNKNOWN_EVENT_TYPE_MSG = "Unknown event type"

private val logger = KotlinLogging.logger {}

@Configuration
open class GenreStreamConfig {

    @Bean
    open fun genreTableProcess(
        @Qualifier("recordNameStrategySerde") valueSerde: Serde<SpecificRecord>
    ): JavaFunction<KStream<String, SpecificRecord>, KTable<String, SpecificRecord>> {
        val materializedStore = getStore("genre-store", valueSerde)

        val materializedGroupBy = getGrouped("genre-grouped", valueSerde)

        return JavaFunction { input ->
            input.filter { _, value ->
                listOf(
                    EventGenreCreated::class.java,
                    EventGenreUpdated::class.java,
                    EventDeleted::class.java
                ).contains(value::class.java)
            }.groupBy({ _, value ->
                when (value) {
                    is EventGenreCreated -> value.data.id
                    is EventGenreUpdated -> value.data.id
                    is EventDeleted -> value.id
                    else -> throw IllegalArgumentException(UNKNOWN_EVENT_TYPE_MSG)
                }
            }, materializedGroupBy).reduce(
                { value, _ -> value },
                materializedStore
            )
        }
    }

    @Bean
    open fun combineGenres(
        findMatchingGenresUseCase: FindMatchingGenresUseCase,
        deduplicateGenreUpdatesUseCase: DeduplicateGenreUpdatesUseCase,
        deleteGenresUseCase: DeleteGenresUseCase,
    ): JavaFunction<KTable<String, SpecificRecord>, KStream<String, SpecificRecord>> {
        return JavaFunction { input ->
            input.toStream().flatMap { _, value ->
                val id = when (value) {
                    is EventGenreCreated -> value.data.id
                    is EventGenreUpdated -> value.data.id
                    is EventDeleted -> value.id
                    else -> throw IllegalArgumentException(UNKNOWN_EVENT_TYPE_MSG)
                }

                val isDeletion = value is EventDeleted

                if (isDeletion && !findMatchingGenresUseCase.isSeenGenreId(id)) {
                    return@flatMap emptyList<KeyValue<String, SpecificRecord>>()
                }

                logger.info { "Processing genre with id $id, event ${value::class.java.simpleName}" }

                val matchingGenres = findMatchingGenresUseCase.findMatchingGenres(id)

                val toSend = matchingGenres
                    .filter { !deduplicateGenreUpdatesUseCase.isUpdateDuplicated(it) }

                val events = toSend.map { combinedGenre ->
                    KeyValue("EventGenreCreated_${combinedGenre.id}", value.toMessageForCombinedGenre(combinedGenre))
                }

                if (isDeletion) {
                    deleteGenresUseCase.deleteGenre(id)
                }

                events
            }
        }
    }

    private fun SpecificRecord.toMessageForCombinedGenre(combinedGenre: CombinedGenre): SpecificRecordBase =
        when (this) {
            is EventGenreCreated -> createCombinedGenre(combinedGenre)
            is EventGenreUpdated -> updateCombinedGenre(combinedGenre)
            is EventDeleted -> deleteCombinedGenre(combinedGenre)
            else -> throw IllegalArgumentException(UNKNOWN_EVENT_TYPE_MSG)
        }

    private fun createCombinedGenre(combinedGenre: CombinedGenre): EventGenreCreated =
        EventGenreCreated.newBuilder().apply {
            data = combinedGenre.toGenreMessage()
            source = "transform"
        }.build()

    private fun updateCombinedGenre(combinedGenre: CombinedGenre): EventGenreUpdated =
        EventGenreUpdated.newBuilder().apply {
            data = combinedGenre.toGenreMessage()
            source = "transform"
        }.build()

    private fun deleteCombinedGenre(combinedGenre: CombinedGenre): EventDeleted =
        EventDeleted.newBuilder().apply {
            id = combinedGenre.id
            source = "transform"
        }.build()

    private fun CombinedGenre.toGenreMessage(): Genre =
        Genre.newBuilder().apply {
            id = this@toGenreMessage.id
            imdbId = this@toGenreMessage.imdbId
            name = this@toGenreMessage.name
        }.build()
}
