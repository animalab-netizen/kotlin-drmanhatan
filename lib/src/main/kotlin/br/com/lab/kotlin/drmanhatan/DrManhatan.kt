package br.com.lab.kotlin.drmanhatan

public class DrManhatan(
    private val bus: EventBus,
    private val factory: EventFactory
) {
    public fun publish(event: Event) {
        bus.publish(event)
    }

    public fun screenViewed(screenName: String) {
        publish(factory.screenViewed(screenName))
    }

    public fun tap(screenName: String, action: String) {
        publish(factory.tap(screenName, action))
    }

    public fun httpError(screenName: String, error: HttpError) {
        publish(factory.httpError(screenName, error))
    }

    public fun custom(name: String, attributes: Map<String, String> = emptyMap()) {
        publish(factory.custom(name, attributes))
    }

    public fun protocolConnectionStarted(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(factory.protocolConnectionStarted(protocol, endpoint, sessionId, attributes))
    }

    public fun protocolConnectionOpened(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(factory.protocolConnectionOpened(protocol, endpoint, sessionId, attributes))
    }

    public fun protocolMessage(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        message: ProtocolMessage,
        sessionId: String? = null
    ) {
        publish(factory.protocolMessage(protocol, endpoint, message, sessionId))
    }

    public fun protocolConnectionClosed(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        close: ProtocolClose = ProtocolClose(),
        sessionId: String? = null
    ) {
        publish(factory.protocolConnectionClosed(protocol, endpoint, close, sessionId))
    }

    public fun protocolFailure(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        failure: ProtocolFailure,
        sessionId: String? = null
    ) {
        publish(factory.protocolFailure(protocol, endpoint, failure, sessionId))
    }

    public fun protocolReconnectScheduled(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        attempt: Int,
        delayMillis: Long,
        reason: String? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(
            factory.protocolReconnectScheduled(
                protocol = protocol,
                endpoint = endpoint,
                attempt = attempt,
                delayMillis = delayMillis,
                reason = reason,
                sessionId = sessionId,
                attributes = attributes
            )
        )
    }

    public fun webSocketConnectionStarted(
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(factory.webSocketConnectionStarted(endpoint, sessionId, attributes))
    }

    public fun webSocketConnectionOpened(
        endpoint: ProtocolEndpoint,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(factory.webSocketConnectionOpened(endpoint, sessionId, attributes))
    }

    public fun webSocketMessageSent(
        endpoint: ProtocolEndpoint,
        operation: String? = null,
        type: String? = null,
        correlationId: String? = null,
        sizeBytes: Long? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(
            factory.webSocketMessageSent(
                endpoint = endpoint,
                operation = operation,
                type = type,
                correlationId = correlationId,
                sizeBytes = sizeBytes,
                sessionId = sessionId,
                attributes = attributes
            )
        )
    }

    public fun webSocketMessageReceived(
        endpoint: ProtocolEndpoint,
        operation: String? = null,
        type: String? = null,
        correlationId: String? = null,
        sizeBytes: Long? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(
            factory.webSocketMessageReceived(
                endpoint = endpoint,
                operation = operation,
                type = type,
                correlationId = correlationId,
                sizeBytes = sizeBytes,
                sessionId = sessionId,
                attributes = attributes
            )
        )
    }

    public fun webSocketConnectionClosed(
        endpoint: ProtocolEndpoint,
        close: ProtocolClose = ProtocolClose(),
        sessionId: String? = null
    ) {
        publish(factory.webSocketConnectionClosed(endpoint, close, sessionId))
    }

    public fun webSocketFailure(
        endpoint: ProtocolEndpoint,
        failure: ProtocolFailure,
        sessionId: String? = null
    ) {
        publish(factory.webSocketFailure(endpoint, failure, sessionId))
    }

    public fun webSocketReconnectScheduled(
        endpoint: ProtocolEndpoint,
        attempt: Int,
        delayMillis: Long,
        reason: String? = null,
        sessionId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        publish(
            factory.webSocketReconnectScheduled(
                endpoint = endpoint,
                attempt = attempt,
                delayMillis = delayMillis,
                reason = reason,
                sessionId = sessionId,
                attributes = attributes
            )
        )
    }

    public fun protocolSession(
        protocol: Protocol,
        endpoint: ProtocolEndpoint,
        sessionId: String? = null
    ): ProtocolSessionTracker = ProtocolSessionTracker(
        drManhatan = this,
        protocol = protocol,
        endpoint = endpoint,
        sessionId = sessionId
    )

    public fun webSocketSession(
        endpoint: ProtocolEndpoint,
        sessionId: String? = null
    ): WebSocketSessionTracker = WebSocketSessionTracker(
        drManhatan = this,
        endpoint = endpoint,
        sessionId = sessionId
    )
}
