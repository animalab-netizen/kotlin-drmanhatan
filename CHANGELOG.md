# Changelog

## 0.1.1

- API publication hardened with Kotlin explicit API mode
- publication target lowered to Java 11 while keeping a Java 22 Gradle runtime for build execution
- `DefaultEventBus` now isolates observer failures so one faulty observer does not block later deliveries
- release documentation updated with compatibility and event bus resilience notes

## 0.1.0

- primeira release publica do `drmanhatan`
- biblioteca Kotlin/JVM sem dependencia de Android
- eventos imutaveis com nome e atributos
- pipeline de enriquecimento via `EventEnricher`
- barramento observavel via `EventBus`
- facade `DrManhatan` para eventos comuns de tela, tap e erro HTTP
- suporte generico a protocolos stateful e orientados a mensagem
- conveniencias iniciais para WebSocket, incluindo conexao, trafego de mensagens, falhas e encerramento
- adapter inicial para OkHttp WebSocket com traducao de callbacks para eventos da linha do tempo de comunicacao
- estrutura separada entre projeto da lib e projeto consumer por artefato Maven
- metadados preparados para GitHub Packages e Sonatype/Maven Central
- documentacao inicial e testes unitarios basicos
