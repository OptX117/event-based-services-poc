package de.fkopf.eventbased.ingest

import com.github.javafaker.Faker
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

/**
 * This application writes streams of randomized data to two kafka topics.
 *
 * It is meant to simulate reading from existing "base" systems,
 * like a system for VoD metadata.
 */

@SpringBootApplication
open class IngestApp() {

    @Bean
    open fun faker() = Faker()

}

fun main(args: Array<String>) {
    runApplication<IngestApp>(*args)
}
