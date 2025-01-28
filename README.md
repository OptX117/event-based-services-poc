# Event Based Services Demo

## Background

The idea is to show that not all data has to always be send at at once. Using an event based architecture, we can send the data in small chunks and process it as needed. Services that do not need a particular event can simply ignore it. At the same time, if data needs to be present "as a set", this can still be achieved by collecting the needed events before processing the data further.

It also serves as a way for me to explore using Spring Cloud Stream, Kafka and what's possible with the Confluent schema registry. So expect some jank. And tests are unnecessary anyway.

## Architecture

This project is composed of three services:

- **Ingest Service**: This service produces a number of initial events and sends them to a Kafka topic.
- **Transform Service**: This service consumes the events from the Kafka topic, transforms them and sends them to another Kafka topic. Some of this transformation happens asynchronously.
- **Query Service**: This service consumes the transformed events and stores them in a database. It also provides a REST API to query the stored events. (Not implemented yet)

## Running the project

### Requirements

Install the following tools:

- Kotlin 
- Java 22
- Docker
- Docker Compose

### Running the infrastructure

Then you can run the following command:

```bash
docker compose -f ./dev-env up
```

This will start the Kafka, Schema Registry and AKHQ services.

Access the AKHQ dashboard at http://localhost:8080

### Running the services

Then register the needed AVRO schemas in the Schema Registry:

```bash
./gradlew :kafkaSchemas:registerSchemasTask
```

Finally, you can start the services, starting with the Ingest Service:

```bash
./gradlew :ingeste:bootRun
```

Then the Transform Service:

```bash
./gradlew :transform:bootRun
```

And finally the Query Service:

```bash
./gradlew :query:bootRun
```
