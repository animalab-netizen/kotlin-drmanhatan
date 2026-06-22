package io.animalab.drmanhatan.okhttp

import io.animalab.drmanhatan.CommonMetadata
import io.animalab.drmanhatan.DefaultEventBus
import io.animalab.drmanhatan.DrManhatan
import io.animalab.drmanhatan.Event
import io.animalab.drmanhatan.EventFactory
import io.animalab.drmanhatan.ProtocolEndpoint
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import kotlin.test.Test
import kotlin.test.assertEquals

class OkHttpWebSocketTelemetryTest {
    @Test
    fun `creates connection started event on telemetry creation by default`() {
        val events = mutableListOf<Event>()
        val tracker = tracker(events)

        tracker.okHttpWebSocketTelemetry(
            OkHttpWebSocketTelemetryConfig(
                endpoint = ProtocolEndpoint(name = "chat"),
                sessionId = "ws-1"
            )
        )

        assertEquals(1, events.size)
        assertEquals("protocol_connection_started", events.single().name)
        assertEquals("websocket", events.single().attributes["protocol.name"])
        assertEquals("ws-1", events.single().attributes["session.id"])
    }

    @Test
    fun `listener maps open message and failure callbacks to drmanhatan events`() {
        val events = mutableListOf<Event>()
        val tracker = tracker(events)
        val telemetry = tracker.okHttpWebSocketTelemetry(
            OkHttpWebSocketTelemetryConfig(
                endpoint = ProtocolEndpoint(name = "chat", address = "wss://socket.example.com"),
                sessionId = "ws-2",
                classifyTextOperation = { "chat_message" },
                connectionStartedOnCreate = false
            )
        )

        telemetry.connectionStarted()
        telemetry.listener.onOpen(FakeWebSocket, response(101, "Switching Protocols"))
        telemetry.listener.onMessage(FakeWebSocket, """{"type":"chat"}""")
        telemetry.listener.onFailure(
            FakeWebSocket,
            java.net.SocketTimeoutException("heartbeat timeout"),
            null
        )

        assertEquals(4, events.size)
        assertEquals("protocol_connection_started", events[0].name)
        assertEquals("protocol_connection_opened", events[1].name)
        assertEquals("101", events[1].attributes["handshake.code"])
        assertEquals("protocol_message", events[2].name)
        assertEquals("chat_message", events[2].attributes["message.operation"])
        assertEquals("text", events[2].attributes["message.type"])
        assertEquals("protocol_failure", events[3].name)
        assertEquals("timeout", events[3].attributes["error.type"])
        assertEquals("true", events[3].attributes["error.retryable"])
    }

    private fun tracker(events: MutableList<Event>): DrManhatan {
        val bus = DefaultEventBus()
        bus.subscribe { events += it }
        return DrManhatan(
            bus = bus,
            factory = EventFactory(
                metadata = CommonMetadata(appVersion = "1.0.0")
            )
        )
    }

    private fun response(code: Int, message: String): Response = Response.Builder()
        .request(Request.Builder().url("https://example.com").build())
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message(message)
        .build()
}

private object FakeWebSocket : okhttp3.WebSocket {
    override fun request(): Request = Request.Builder().url("https://example.com").build()

    override fun queueSize(): Long = 0L

    override fun send(text: String): Boolean = true

    override fun send(bytes: okio.ByteString): Boolean = true

    override fun close(code: Int, reason: String?): Boolean = true

    override fun cancel() = Unit
}
