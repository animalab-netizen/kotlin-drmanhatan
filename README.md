# kotlin-drmanhatan

[![CI](https://github.com/animalab-netizen/kotlin-drmanhatan/actions/workflows/ci.yml/badge.svg)](https://github.com/animalab-netizen/kotlin-drmanhatan/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/animalab-netizen/kotlin-drmanhatan/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.animalab-netizen/kotlin-drmanhatan)](https://central.sonatype.com/search?q=io.github.animalab-netizen%3Akotlin-drmanhatan)

`kotlin-drmanhatan` is a Kotlin library by ÂnimaLab for teams that want a stable and explicit way to observe application events and communication flows without coupling domain code to analytics, telemetry or transport vendors.

The project provides a small runtime for:

- immutable event modeling
- event enrichment pipelines
- observer-based event delivery
- communication and protocol telemetry
- session-oriented tracking for stateful channels
- optional integration with real transport engines such as OkHttp WebSocket

The goal is to make event flow easier to standardize, easier to reason about, and less vulnerable to common implementation mistakes around vendor coupling, protocol lifecycle tracking, retry visibility and message-oriented observability.

## Why Use DrManhatan

`kotlin-drmanhatan` is useful when a system needs observability but should not let transport or telemetry concerns leak into business code.

Typical gains include:

- a single event vocabulary across UI, network and protocol layers
- lower coupling to analytics, logging and monitoring vendors
- easier migration between providers because the domain emits neutral events
- more explicit communication timelines for stateful protocols
- better operational visibility during failures, retries and reconnect flows

In practice, this means teams can treat observability as part of architecture instead of as scattered implementation detail.

## Asynchronous Work

The library is especially useful in asynchronous and message-oriented systems.

Asynchrony usually makes systems harder to interpret because cause and effect are separated in time:

- a connection starts in one place
- a message arrives later in another place
- a retry is scheduled elsewhere
- an error is observed after the original action has already left the current call stack

`kotlin-drmanhatan` improves this by giving those steps a shared event model and a shared session context.

This helps teams:

- reconstruct communication timelines more reliably
- track retries and reconnects without inventing ad hoc logging everywhere
- correlate outbound intent with inbound outcomes
- expose protocol failures with enough metadata to support analysis and debugging
- keep asynchronous work observable without forcing domain code to know the final monitoring backend

## What DrManhatan Does Not Claim

`kotlin-drmanhatan` does not try to replace your network stack, your analytics provider or your monitoring backend.

It does not open WebSocket connections, execute HTTP calls or guarantee that every team will model events with identical naming conventions. The library is intentionally narrower than that: it standardizes event construction, enrichment and publication so the rest of the system can evolve without forcing the domain layer to know too much about the final destination of those events.

The reason is pragmatic: a communication observability library should make transport and vendor integration easier, not become another hard dependency that application code cannot escape later.

## Repository

- source: [github.com/animalab-netizen/kotlin-drmanhatan](https://github.com/animalab-netizen/kotlin-drmanhatan)

## Status

`kotlin-drmanhatan` is in early stage and evolving toward public Maven/Gradle distribution.

The API is usable and already validated through a separate consumer project, but it is still under refinement. Expect incremental improvements in publication maturity, adapter coverage and protocol semantics as the library evolves.

## Coordinates

Current coordinates:

- `groupId`: `io.github.animalab-netizen`
- `artifactId`: `kotlin-drmanhatan`
- `version`: `0.1.0`

Dependency:

```gradle
dependencies {
    implementation "io.github.animalab-netizen:kotlin-drmanhatan:0.1.0"
}
```

## Repositories

For local development:

```gradle
repositories {
    mavenLocal()
}
```

For public distribution, prefer Maven Central:

```gradle
repositories {
    mavenCentral()
}
```

## Installation

Example `build.gradle`:

```gradle
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation "io.github.animalab-netizen:kotlin-drmanhatan:0.1.0"
}
```

## Public API

The intended public surface of `kotlin-drmanhatan` is centered on these concepts:

- `Event`
- `EventEnricher`
- `EventBus`
- `DefaultEventBus`
- `EventFactory`
- `DrManhatan`
- `Protocol`
- `ProtocolEndpoint`
- `ProtocolMessage`
- `ProtocolFailure`
- `ProtocolClose`
- `ProtocolSessionTracker`
- `WebSocketSessionTracker`
- `OkHttpWebSocketTelemetry`
- `OkHttpWebSocketTelemetryConfig`

Internal publication details, local scripts and workflow implementation details are intentionally not part of the product contract and may change without notice.

## Core Concepts

### 1. Event

`Event` is the immutable unit of observation in the library.

It gives the system:

- a stable event name
- explicit string attributes
- a simple representation that can be forwarded to logs, analytics, metrics or custom observers

### 2. Enricher

`EventEnricher` adds context without requiring the producer to know the final destination of the event.

Typical usage includes:

- app version
- platform
- environment
- protocol metadata
- transport-specific attributes

### 3. EventBus

`EventBus` publishes events to one or more observers.

This keeps event production separate from event handling, so application code does not need to bind directly to a specific monitoring vendor or output mechanism.

### 4. EventFactory

`EventFactory` is the main construction layer for common event categories.

It provides helpers for:

- screen events
- tap events
- HTTP failures
- protocol connection lifecycle
- message-oriented communication events
- WebSocket-specific convenience builders

### 5. Protocol Sessions

`ProtocolSessionTracker` and `WebSocketSessionTracker` provide a higher-level model for stateful communication.

This is the preferred abstraction when the important thing is not one isolated event, but the whole communication timeline:

- connection started
- connection opened
- inbound and outbound messages
- heartbeat signals
- reconnect scheduling
- failure
- close

## Protocol Coverage

`kotlin-drmanhatan` covers stateful and message-oriented protocols without depending on a specific transport engine.

This includes use cases such as:

- WebSocket
- SSE
- gRPC streaming
- MQTT
- custom TCP/UDP communication channels

The purpose is not transport execution. The purpose is observability of protocol lifecycle and message flow.

## Basic Example

```kotlin
import br.com.lab.kotlin.drmanhatan.CommonMetadata
import br.com.lab.kotlin.drmanhatan.DefaultEventBus
import br.com.lab.kotlin.drmanhatan.DrManhatan
import br.com.lab.kotlin.drmanhatan.EventFactory
import br.com.lab.kotlin.drmanhatan.HttpError

val bus = DefaultEventBus()
bus.subscribe { event ->
    println("${event.name} -> ${event.attributes}")
}

val tracker = DrManhatan(
    bus = bus,
    factory = EventFactory(
        metadata = CommonMetadata(
            appVersion = "1.0.0",
            platform = "android",
            environment = "prod"
        )
    )
)

tracker.screenViewed("Home")
tracker.tap("Home", "OpenProfile")
tracker.httpError("Checkout", HttpError(code = 500, type = "server_error"))
```

## WebSocket Example

```kotlin
import br.com.lab.kotlin.drmanhatan.CommonMetadata
import br.com.lab.kotlin.drmanhatan.DefaultEventBus
import br.com.lab.kotlin.drmanhatan.DrManhatan
import br.com.lab.kotlin.drmanhatan.EventFactory
import br.com.lab.kotlin.drmanhatan.ProtocolClose
import br.com.lab.kotlin.drmanhatan.ProtocolEndpoint
import br.com.lab.kotlin.drmanhatan.ProtocolFailure

val bus = DefaultEventBus()
bus.subscribe { event ->
    println("${event.name} -> ${event.attributes}")
}

val tracker = DrManhatan(
    bus = bus,
    factory = EventFactory(
        metadata = CommonMetadata(
            appVersion = "1.0.0",
            platform = "android",
            environment = "prod"
        )
    )
)

val endpoint = ProtocolEndpoint(
    name = "chat",
    address = "wss://socket.example.com",
    channel = "rooms/general"
)

tracker.webSocketConnectionStarted(endpoint, sessionId = "ws-42")
tracker.webSocketConnectionOpened(endpoint, sessionId = "ws-42")
tracker.webSocketMessageSent(
    endpoint = endpoint,
    operation = "join_room",
    type = "json",
    correlationId = "corr-1",
    sessionId = "ws-42"
)
tracker.webSocketMessageReceived(
    endpoint = endpoint,
    operation = "chat_message",
    type = "json",
    sizeBytes = 512,
    sessionId = "ws-42"
)
tracker.webSocketFailure(
    endpoint = endpoint,
    failure = ProtocolFailure(
        code = "WS_TIMEOUT",
        type = "transport",
        message = "heartbeat timeout",
        retryable = true
    ),
    sessionId = "ws-42"
)
tracker.webSocketConnectionClosed(
    endpoint = endpoint,
    close = ProtocolClose(code = 1001, reason = "going away", graceful = false),
    sessionId = "ws-42"
)
```

## Session Example

```kotlin
val session = tracker.webSocketSession(
    endpoint = ProtocolEndpoint(
        name = "chat",
        address = "wss://socket.example.com",
        channel = "rooms/general"
    ),
    sessionId = "ws-42"
)

session.connectionStarted()
session.connectionOpened()
session.outboundMessage(operation = "join_room", type = "json", correlationId = "corr-1")
session.heartbeatReceived(correlationId = "hb-1")
session.reconnectScheduled(
    attempt = 2,
    delayMillis = 2_000,
    reason = "network_lost"
)
session.closed(
    ProtocolClose(code = 1001, reason = "going away", graceful = false)
)
```

## OkHttp WebSocket Example

```kotlin
import br.com.lab.kotlin.drmanhatan.ProtocolEndpoint
import br.com.lab.kotlin.drmanhatan.okhttp.OkHttpWebSocketTelemetryConfig
import br.com.lab.kotlin.drmanhatan.okhttp.okHttpWebSocketTelemetry
import okhttp3.OkHttpClient
import okhttp3.Request

val client = OkHttpClient()
val telemetry = tracker.okHttpWebSocketTelemetry(
    OkHttpWebSocketTelemetryConfig(
        endpoint = ProtocolEndpoint(
            name = "chat",
            address = "wss://socket.example.com",
            channel = "rooms/general"
        ),
        sessionId = "ws-42",
        classifyTextOperation = { payload ->
            when {
                "\"join_room\"" in payload -> "join_room"
                "\"chat_message\"" in payload -> "chat_message"
                else -> null
            }
        }
    )
)

client.newWebSocket(
    Request.Builder().url("wss://socket.example.com").build(),
    telemetry.listener
)
```

## Consumer Project

The separate consumer project lives locally in this workspace at [ConsumerApp.kt](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/kotlin-drmanhatan-consumer/src/main/kotlin/io/animalab/drmanhatan/consumer/ConsumerApp.kt).

Its purpose is to validate the real adoption mode:

- the library is published as an artifact
- consumers import the artifact through dependencies
- consumers do not depend on `project(":lib")`

## Publishing

Publish locally:

```bash
./scripts/publish-local.sh
```

Publish to GitHub Packages:

```bash
source ./scripts/env.sh
./gradlew :lib:publishMavenJavaPublicationToGitHubPackagesRepository
```

Publish to Sonatype / Maven Central:

```bash
source ./scripts/env.sh
./gradlew :lib:publishMavenJavaPublicationToSonatypeRepository
```

For Sonatype publishing, provide credentials and signing material through CI secrets or untracked Gradle properties.

## Local Validation

Run the library tests:

```bash
./scripts/test-lib.sh
```

Run the full local flow from the workspace root:

```bash
./scripts/bootstrap.sh
```

This executes:

1. library tests
2. local publication to `mavenLocal`
3. separate consumer execution

## Compatibility Notes

The library is Kotlin/JVM-first and currently validated with Java 22 in local and CI environments.

The OkHttp integration is optional and intentionally kept as an adapter layer so the core runtime remains independent from transport vendors.

## Contributing

See [CONTRIBUTING.md](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/kotlin-drmanhatan/CONTRIBUTING.md).

## Changelog

See [CHANGELOG.md](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/kotlin-drmanhatan/CHANGELOG.md).

## Maintainer

- name: `ÂnimaLab`
- email: `animalab.desenvolvimento@gmail.com`

## License

This project is licensed under Apache-2.0. See [LICENSE](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/kotlin-drmanhatan/LICENSE).
