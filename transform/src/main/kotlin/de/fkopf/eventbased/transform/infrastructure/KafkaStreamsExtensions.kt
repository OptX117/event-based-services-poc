package de.fkopf.eventbased.transform.infrastructure

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.Grouped
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.state.BuiltInDslStoreSuppliers
import org.apache.kafka.streams.state.KeyValueStore

fun <T : Any> getStore(name: String, valueSerde: Serde<T>) =
    Materialized.`as`<String, T, KeyValueStore<org.apache.kafka.common.utils.Bytes, ByteArray>>(name)
        .withValueSerde(valueSerde).withKeySerde(Serdes.String()).withStoreType(BuiltInDslStoreSuppliers.IN_MEMORY)

fun <T : Any> getGrouped(name: String, valueSerde: Serde<T>) =
    Grouped.`as`<String, T>(name)
        .withValueSerde(valueSerde).withKeySerde(Serdes.String())
