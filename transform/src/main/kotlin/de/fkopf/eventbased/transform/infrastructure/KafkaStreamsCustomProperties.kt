package de.fkopf.eventbased.transform.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kafka.streams")
class KafkaStreamsCustomProperties {
    val schemaRegistryUrl = "http://localhost:8081"
}
