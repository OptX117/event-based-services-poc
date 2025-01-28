package de.fkopf.eventbased.transform.infrastructure

import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.common.serialization.Serde
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(KafkaStreamsCustomProperties::class)
open class KafkaStreamsConfig {

    @Bean
    open fun recordNameStrategySerde(
        properties: KafkaStreamsCustomProperties
    ): Serde<SpecificRecord> {
        val valueSerde =
            io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde<SpecificRecord>()

        valueSerde.configure(
            mapOf(
                "schema.registry.url" to properties.schemaRegistryUrl,
                KafkaAvroDeserializerConfig.VALUE_SUBJECT_NAME_STRATEGY to "io.confluent.kafka.serializers.subject.RecordNameStrategy"
            ),
            false
        )

        return valueSerde
    }
}
