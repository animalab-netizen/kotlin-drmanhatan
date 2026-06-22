# kotlin-drmanhatan

[![CI](https://github.com/animalab-netizen/kotlin-drmanhatan/actions/workflows/ci.yml/badge.svg)](https://github.com/animalab-netizen/kotlin-drmanhatan/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/animalab-netizen/kotlin-drmanhatan/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.animalab/drmanhatan)](https://central.sonatype.com/search?q=io.animalab%3Adrmanhatan)

`drmanhatan` e uma biblioteca Kotlin para observabilidade de eventos sem acoplar o dominio a um provider de analytics, log ou telemetria.

Repositorio oficial planejado:

- [animalab-netizen/kotlin-drmanhatan](https://github.com/animalab-netizen/kotlin-drmanhatan)

Coordenada Maven planejada:

- `io.animalab:drmanhatan`

O repositorio da biblioteca fica isolado do consumer, para refletir o alvo real de publicacao:

- `kotlin-drmanhatan`: projeto da biblioteca
- `kotlin-drmanhatan-consumer`: projeto separado que consome o artefato por coordenada Maven

Ela nasce a partir da analise do modulo `core-event`, preservando a ideia central e removendo os pontos que impediam publicacao como produto open source:

- sem dependencia de Android
- sem reflection para criar trackers
- sem estado oculto em builders
- eventos imutaveis e tipados
- observadores desacoplados do destino final

## Diagnostico do `core-event`

O conceito do modulo original e valido. Ele resolve um problema real: enriquecer eventos de interface e infraestrutura sem criar dependencia direta com Firebase, Datadog, New Relic ou qualquer handler concreto.

Os principais problemas encontrados foram:

- o projeto atual e uma Android Library, embora o nucleo nao precise de Android
- a API usa reflection em `EventFactoryMethod.Build`, o que fragiliza construcao e testes
- `CommonEventConfig` fixa `appVersion = "0.0.1"`, o que impede uso real em producao
- toda a semantica do evento e uma `Map<String, String>` sem identidade explicita do evento
- o build esta misturando fonte de biblioteca com artefatos locais de IDE e Android Studio

## Arquitetura da nova lib

`drmanhatan` separa o problema em quatro partes:

1. `Event`: evento imutavel com nome e atributos.
2. `EventEnricher`: componente que adiciona contexto sem conhecer o destino.
3. `EventBus`: publica eventos para um ou mais observadores.
4. `DrManhatan`: facade que cria eventos comuns e dispara o pipeline.

## Suporte a protocolos

O `drmanhatan` agora tambem cobre protocolos stateful e orientados a mensagem sem depender da stack de rede. Isso e util quando a ODE ou outra lib de dominio ainda nao consegue representar bem:

- WebSocket
- SSE
- gRPC streaming
- MQTT
- TCP/UDP customizados

O papel da lib aqui nao e abrir conexoes. O papel dela e padronizar telemetria de:

- tentativa de conexao
- conexao aberta
- trafego de mensagens de entrada e saida
- heartbeat e health signals
- reconexao e politica de retry
- encerramento de sessao
- falhas de protocolo e transporte

## Integracao com engines reais

O nucleo continua independente de transporte, mas a biblioteca agora tambem oferece uma integracao inicial com OkHttp WebSocket para JVM/Android. A integracao traduz callbacks do `WebSocketListener` para eventos do `drmanhatan`.

## Exemplo de uso

```kotlin
import io.animalab.drmanhatan.CommonMetadata
import io.animalab.drmanhatan.DefaultEventBus
import io.animalab.drmanhatan.DrManhatan
import io.animalab.drmanhatan.EventFactory
import io.animalab.drmanhatan.HttpError

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

## Estrutura do projeto

```text
kotlin-drmanhatan-projects/
  kotlin-drmanhatan/
    lib/
      src/main/kotlin/...
      src/test/kotlin/...
  kotlin-drmanhatan-consumer/
```

O codigo-fonte da biblioteca fica em `lib/src/main/kotlin` e os testes em `lib/src/test/kotlin`. O consumer fica fora do build da lib para simular o uso futuro por dependencia publicada.

### Exemplo com WebSocket

```kotlin
import io.animalab.drmanhatan.CommonMetadata
import io.animalab.drmanhatan.DefaultEventBus
import io.animalab.drmanhatan.DrManhatan
import io.animalab.drmanhatan.EventFactory
import io.animalab.drmanhatan.ProtocolClose
import io.animalab.drmanhatan.ProtocolEndpoint
import io.animalab.drmanhatan.ProtocolFailure

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

### Exemplo com sessao de comunicacao

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

### Exemplo com OkHttp WebSocket

```kotlin
import io.animalab.drmanhatan.ProtocolEndpoint
import io.animalab.drmanhatan.okhttp.OkHttpWebSocketTelemetryConfig
import io.animalab.drmanhatan.okhttp.okHttpWebSocketTelemetry
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

## Consumer de exemplo

O consumer executavel fica em [ConsumerApp.kt](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/kotlin-drmanhatan-consumer/src/main/kotlin/io/animalab/drmanhatan/consumer/ConsumerApp.kt) e demonstra:

- eventos de tela e tap
- erro HTTP
- sessao WebSocket observada diretamente pelo `drmanhatan`
- integracao com callbacks do OkHttp WebSocket

Ele consome a biblioteca por coordenada Maven, nao por `project(":lib")`.

Quando o ambiente tiver Gradle disponivel, o fluxo local esperado e:

```bash
./scripts/test-lib.sh
./scripts/publish-local.sh
```

Se quiser executar manualmente pelo wrapper:

```bash
source ./scripts/env.sh
./gradlew :lib:test
./gradlew :lib:publishToMavenLocal
```

## Publicacao

A estrutura ja esta preparada para publicacao Maven via `maven-publish` e ja conta com base inicial de open source:

- `LICENSE`
- `CHANGELOG.md`
- CI

Ainda faltam para um lancamento publico formal:

- assinatura/publicacao real
- definicao final de coordenadas Maven da AnimaLab
- repositorio publico oficial
- validacao juridica do nome

### Destinos preparados

- GitHub Packages: `https://maven.pkg.github.com/animalab-netizen/kotlin-drmanhatan`
- Sonatype OSSRH / Maven Central: `s01.oss.sonatype.org`

### Credenciais esperadas

GitHub Packages:

- `GITHUB_ACTOR` e `GITHUB_TOKEN`
- ou `gpr.user` e `gpr.key`

Sonatype:

- `OSSRH_USERNAME` e `OSSRH_PASSWORD`
- ou `ossrhUsername` e `ossrhPassword`

Assinatura:

- `SIGNING_KEY` e `SIGNING_PASSWORD`
- ou `signingKey` e `signingPassword`

### Comandos de publicacao

GitHub Packages:

```bash
source ./scripts/env.sh
./gradlew :lib:publishMavenJavaPublicationToGitHubPackagesRepository
```

Sonatype / Maven Central:

```bash
source ./scripts/env.sh
./gradlew :lib:publishMavenJavaPublicationToSonatypeRepository
```

## Operacao local

Este repositorio inclui scripts locais para evitar repetir configuracao de shell:

- [scripts/env.sh](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/drmanhatan/scripts/env.sh)
- [scripts/test-lib.sh](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/drmanhatan/scripts/test-lib.sh)
- [scripts/publish-local.sh](/Users/caiosanchezchristino/Desktop/drmanhatan-projects/drmanhatan/scripts/publish-local.sh)

## Nota de marca

O nome `drmanhatan` funciona como nome tecnico interno nesta primeira versao, mas como ele foi inspirado explicitamente em um personagem da DC Comics, vale uma revisao juridica antes de publicacao publica para reduzir risco de marca.
