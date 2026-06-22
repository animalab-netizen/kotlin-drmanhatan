package br.com.lab.kotlin.drmanhatan

public class EventFactory(
    metadata: CommonMetadata? = null,
    private val customEnrichers: List<EventEnricher> = emptyList()
) {
    private val enrichers = buildList {
        if (metadata != null) {
            add(CommonMetadataEnricher(metadata))
        }
        addAll(customEnrichers)
    }

    public fun screenViewed(screenName: String): Event =
        enrich(
            Event(
                name = "screen_viewed",
                attributes = mapOf("screen.name" to screenName)
            )
        )

    public fun tap(screenName: String, action: String): Event =
        enrich(
            Event(
                name = "tap",
                attributes = mapOf(
                    "screen.name" to screenName,
                    "event.action" to action
                )
            )
        )

    public fun httpError(screenName: String, error: HttpError): Event =
        enrich(
            Event(
                name = "http_error",
                attributes = buildMap {
                    put("screen.name", screenName)
                    put("error.code", error.code.toString())
                    error.type?.let { put("error.type", it) }
                    error.message?.let { put("error.message", it) }
                }
            )
        )

    public fun custom(name: String, attributes: Map<String, String> = emptyMap()): Event =
        enrich(Event(name = name, attributes = attributes))

    public fun protocolConnectionStarted(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = enrichProtocolEvent(
        name = "protocol_connection_started",
        protocol = protocol,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = attributes
    )

    public fun protocolConnectionOpened(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = enrichProtocolEvent(
        name = "protocol_connection_opened",
        protocol = protocol,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = attributes
    )

    public fun protocolMessage(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        message: ProtocolMessage,
        sessionId: String? = null
    ): Event = enrichProtocolEvent(
        name = "protocol_message",
        protocol = protocol,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = message.asAttributes()
    )

    public fun protocolConnectionClosed(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        close: ProtocolClose = ProtocolClose(),
        sessionId: String? = null
    ): Event = enrichProtocolEvent(
        name = "protocol_connection_closed",
        protocol = protocol,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = close.asAttributes()
    )

    public fun protocolFailure(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        failure: ProtocolFailure,
        sessionId: String? = null
    ): Event = enrichProtocolEvent(
        name = "protocol_failure",
        protocol = protocol,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = failure.asAttributes()
    )

    public fun protocolReconnectScheduled(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        attempt: Int,
        delayMillis: Long,
        reason: String? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = enrichProtocolEvent(
        name = "protocol_reconnect_scheduled",
        protocol = protocol,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = buildMap {
            put("reconnect.attempt", attempt.toString())
            put("reconnect.delay_ms", delayMillis.toString())
            reason?.let { put("reconnect.reason", it) }
            putAll(attributes)
        }
    )

    public fun webSocketConnectionStarted(
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = protocolConnectionStarted(
        protocol = Protocol.WebSocket,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = attributes
    )

    public fun webSocketConnectionOpened(
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = protocolConnectionOpened(
        protocol = Protocol.WebSocket,
        endpoint = endpoint,
        sessionId = sessionId,
        attributes = attributes
    )

    public fun webSocketMessageSent(
        endpoint: ProtocolEndpoint,
        operation: String? = null,
        type: String? = null,
        correlationId: String? = null,
        sizeBytes: Long? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = protocolMessage(
        protocol = Protocol.WebSocket,
        endpoint = endpoint,
        message = ProtocolMessage(
            direction = ProtocolMessageDirection.OUTBOUND,
            operation = operation,
            type = type,
            correlationId = correlationId,
            sizeBytes = sizeBytes,
            attributes = attributes
        ),
        sessionId = sessionId
    )

    public fun webSocketMessageReceived(
        endpoint: ProtocolEndpoint,
        operation: String? = null,
        type: String? = null,
        correlationId: String? = null,
        sizeBytes: Long? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = protocolMessage(
        protocol = Protocol.WebSocket,
        endpoint = endpoint,
        message = ProtocolMessage(
            direction = ProtocolMessageDirection.INBOUND,
            operation = operation,
            type = type,
            correlationId = correlationId,
            sizeBytes = sizeBytes,
            attributes = attributes
        ),
        sessionId = sessionId
    )

    public fun webSocketConnectionClosed(
        endpoint: ProtocolEndpoint,
        close: ProtocolClose = ProtocolClose(),
        sessionId: String? = null
    ): Event = protocolConnectionClosed(
        protocol = Protocol.WebSocket,
        endpoint = endpoint,
        close = close,
        sessionId = sessionId
    )

    public fun webSocketFailure(
        endpoint: ProtocolEndpoint,
        failure: ProtocolFailure,
        sessionId: String? = null
    ): Event = protocolFailure(
        protocol = Protocol.WebSocket,
        endpoint = endpoint,
        failure = failure,
        sessionId = sessionId
    )

    public fun webSocketReconnectScheduled(
        endpoint: ProtocolEndpoint,
        attempt: Int,
        delayMillis: Long,
        reason: String? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ): Event = protocolReconnectScheduled(
        protocol = Protocol.WebSocket,
        endpoint = endpoint,
        attempt = attempt,
        delayMillis = delayMillis,
        reason = reason,
        sessionId = sessionId,
        attributes = attributes
    )

    private fun enrich(event: Event): Event =
        enrichers.fold(event) { current, enricher -> enricher.enrich(current) }

    private fun enrichProtocolEvent(
        name: String,
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        sessionId: String?,
        attributes: Map<String, String>
    ): Event = enrich(
        Event(
            name = name,
            attributes = buildMap {
                put("protocol.name", protocol.name)
                putAll(endpoint.asAttributes())
                sessionId?.let { put("session.id", it) }
                putAll(attributes)
            }
        )
    )
}
