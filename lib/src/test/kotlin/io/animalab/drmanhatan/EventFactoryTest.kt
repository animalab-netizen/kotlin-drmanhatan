package io.animalab.drmanhatan

import kotlin.test.Test
import kotlin.test.assertEquals

class EventFactoryTest {
    private val factory = EventFactory(
        metadata = CommonMetadata(
            appVersion = "2.0.0",
            platform = "android",
            environment = "prod"
        )
    )

    @Test
    fun `builds screen event with common metadata`() {
        val event = factory.screenViewed("Feed")

        assertEquals("screen_viewed", event.name)
        assertEquals("Feed", event.attributes["screen.name"])
        assertEquals("2.0.0", event.attributes["app.version"])
        assertEquals("prod", event.attributes["environment"])
    }

    @Test
    fun `builds tap event`() {
        val event = factory.tap("Feed", "OpenPost")

        assertEquals("tap", event.name)
        assertEquals("Feed", event.attributes["screen.name"])
        assertEquals("OpenPost", event.attributes["event.action"])
    }

    @Test
    fun `builds http error event`() {
        val event = factory.httpError(
            screenName = "Checkout",
            error = HttpError(
                code = 504,
                type = "timeout",
                message = "gateway timeout"
            )
        )

        assertEquals("http_error", event.name)
        assertEquals("504", event.attributes["error.code"])
        assertEquals("timeout", event.attributes["error.type"])
        assertEquals("gateway timeout", event.attributes["error.message"])
    }

    @Test
    fun `builds websocket message event`() {
        val event = factory.webSocketMessageReceived(
            endpoint = ProtocolEndpoint(
                name = "chat",
                address = "wss://socket.example.com",
                channel = "rooms/general"
            ),
            operation = "chat_message",
            type = "json",
            correlationId = "corr-1",
            sizeBytes = 128,
            sessionId = "session-123"
        )

        assertEquals("protocol_message", event.name)
        assertEquals("websocket", event.attributes["protocol.name"])
        assertEquals("chat", event.attributes["endpoint.name"])
        assertEquals("wss://socket.example.com", event.attributes["endpoint.address"])
        assertEquals("rooms/general", event.attributes["endpoint.channel"])
        assertEquals("inbound", event.attributes["message.direction"])
        assertEquals("chat_message", event.attributes["message.operation"])
        assertEquals("json", event.attributes["message.type"])
        assertEquals("corr-1", event.attributes["message.correlation_id"])
        assertEquals("128", event.attributes["message.size_bytes"])
        assertEquals("session-123", event.attributes["session.id"])
    }

    @Test
    fun `builds protocol failure event`() {
        val event = factory.protocolFailure(
            protocol = Protocol.Grpc,
            endpoint = ProtocolEndpoint(name = "billing-stream"),
            failure = ProtocolFailure(
                code = "UNAVAILABLE",
                type = "transport",
                message = "upstream unavailable",
                retryable = true
            ),
            sessionId = "stream-7"
        )

        assertEquals("protocol_failure", event.name)
        assertEquals("grpc", event.attributes["protocol.name"])
        assertEquals("billing-stream", event.attributes["endpoint.name"])
        assertEquals("UNAVAILABLE", event.attributes["error.code"])
        assertEquals("transport", event.attributes["error.type"])
        assertEquals("upstream unavailable", event.attributes["error.message"])
        assertEquals("true", event.attributes["error.retryable"])
        assertEquals("stream-7", event.attributes["session.id"])
    }

    @Test
    fun `builds protocol reconnect event`() {
        val event = factory.webSocketReconnectScheduled(
            endpoint = ProtocolEndpoint(name = "presence"),
            attempt = 3,
            delayMillis = 1500,
            reason = "heartbeat_timeout",
            sessionId = "ws-7"
        )

        assertEquals("protocol_reconnect_scheduled", event.name)
        assertEquals("websocket", event.attributes["protocol.name"])
        assertEquals("presence", event.attributes["endpoint.name"])
        assertEquals("3", event.attributes["reconnect.attempt"])
        assertEquals("1500", event.attributes["reconnect.delay_ms"])
        assertEquals("heartbeat_timeout", event.attributes["reconnect.reason"])
        assertEquals("ws-7", event.attributes["session.id"])
    }
}
