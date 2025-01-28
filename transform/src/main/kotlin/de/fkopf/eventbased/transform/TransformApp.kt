package de.fkopf.eventbased.transform

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


/**
 * This application reads events from a topic, then transforms some of the events into other events.
 *
 * It is meant to simulate reacting to events and enriching them with additional data or trigger workflows.
 */

@SpringBootApplication
open class TransformApp()

fun main(args: Array<String>) {
    runApplication<TransformApp>(*args)
}
