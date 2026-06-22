package br.com.lab.kotlin.drmanhatan

open class ProtocolSessionTracker(
    private val drManhatan: DrManhatan,
    private val protocol: Protocol,
    val endpoint: ProtocolEndpoint,
    val sessionId: String? = null
) {
    fun connectionStarted(attributes: Map<String, String> = emptyMap()) {
        drManhatan.protocolConnectionStarted(protocol, endpoint, sessionId, attributes)
    }

    fun connectionOpened(attributes: Map<String, String> = emptyMap()) {
        drManhatan.protocolConnectionOpened(protocol, endpoint, sessionId, attributes)
    }

    fun message(message: ProtocolMessage) {
        drManhatan.protocolMessage(protocol, endpoint, message, sessionId)
    }

    fun inboundMessage(
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

    fun outboundMessage(
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

    fun heartbeatSent(
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

    fun heartbeatReceived(
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

    fun reconnectScheduled(
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

    fun failure(failure: ProtocolFailure) {
        drManhatan.protocolFailure(protocol, endpoint, failure, sessionId)
    }

    fun closed(close: ProtocolClose = ProtocolClose()) {
        drManhatan.protocolConnectionClosed(protocol, endpoint, close, sessionId)
    }
}
