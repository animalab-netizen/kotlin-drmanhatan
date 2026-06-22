package br.com.lab.kotlin.drmanhatan

public open class ProtocolSessionTracker(
    private val drManhatan: DrManhatan,
    private val protocol: Protocol,
    public val endpoint: ProtocolEndpoint,
    public val sessionId: String? = null
) {
    public fun connectionStarted(attributes: Map<String, String> = emptyMap()) {
        drManhatan.protocolConnectionStarted(protocol, endpoint, sessionId, attributes)
    }

    public fun connectionOpened(attributes: Map<String, String> = emptyMap()) {
        drManhatan.protocolConnectionOpened(protocol, endpoint, sessionId, attributes)
    }

    public fun message(message: ProtocolMessage) {
        drManhatan.protocolMessage(protocol, endpoint, message, sessionId)
    }

    public fun inboundMessage(
        operation: String? = null,
        type: String? = null,
        correlationId: String? = null,
        sizeBytes: Long? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        message(
            ProtocolMessage(
                direction = ProtocolMessageDirection.INBOUND,
                operation = operation,
                type = type,
                correlationId = correlationId,
                sizeBytes = sizeBytes,
                attributes = attributes
            )
        )
    }

    public fun outboundMessage(
        operation: String? = null,
        type: String? = null,
        correlationId: String? = null,
        sizeBytes: Long? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        message(
            ProtocolMessage(
                direction = ProtocolMessageDirection.OUTBOUND,
                operation = operation,
                type = type,
                correlationId = correlationId,
                sizeBytes = sizeBytes,
                attributes = attributes
            )
        )
    }

    public fun heartbeatSent(
        correlationId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        outboundMessage(
            operation = "heartbeat",
            type = "heartbeat",
            correlationId = correlationId,
            attributes = attributes
        )
    }

    public fun heartbeatReceived(
        correlationId: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        inboundMessage(
            operation = "heartbeat",
            type = "heartbeat",
            correlationId = correlationId,
            attributes = attributes
        )
    }

    public fun reconnectScheduled(
        attempt: Int,
        delayMillis: Long,
        reason: String? = null,
        attributes: Map<String, String> = emptyMap()
    ) {
        drManhatan.protocolReconnectScheduled(
            protocol = protocol,
            endpoint = endpoint,
            attempt = attempt,
            delayMillis = delayMillis,
            reason = reason,
            sessionId = sessionId,
            attributes = attributes
        )
    }

    public fun failure(failure: ProtocolFailure) {
        drManhatan.protocolFailure(protocol, endpoint, failure, sessionId)
    }

    public fun closed(close: ProtocolClose = ProtocolClose()) {
        drManhatan.protocolConnectionClosed(protocol, endpoint, close, sessionId)
    }
}
