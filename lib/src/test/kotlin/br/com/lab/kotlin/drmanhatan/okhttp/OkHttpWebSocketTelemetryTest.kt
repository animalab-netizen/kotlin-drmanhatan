package br.com.lab.kotlin.drmanhatan.okhttp

import br.com.lab.kotlin.drmanhatan.CommonMetadata
import br.com.lab.kotlin.drmanhatan.DefaultEventBus
import br.com.lab.kotlin.drmanhatan.DrManhatan
import br.com.lab.kotlin.drmanhatan.Event
import br.com.lab.kotlin.drmanhatan.EventFactory
import br.com.lab.kotlin.drmanhatan.ProtocolEndpoint
import br.com.lab.kotlin.drmanhatan.ProtocolFailure
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okio.ByteString
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

    @Test
    fun `listener maps binary closing and closed callbacks`() {
        val events = mutableListOf<Event>()
        val tracker = tracker(events)
        val telemetry = tracker.okHttpWebSocketTelemetry(
            OkHttpWebSocketTelemetryConfig(
                endpoint = ProtocolEndpoint(name = "binary-chat"),
                classifyBinaryOperation = { "binary_frame" },
                connectionStartedOnCreate = false
            )
        )

        telemetry.connectionStarted()
        telemetry.listener.onMessage(FakeWebSocket, ByteString.of(*"010101".toByteArray()))
        telemetry.listener.onClosing(FakeWebSocket, 1000, "normal closure")
        telemetry.listener.onClosed(FakeWebSocket, 1000, "normal closure")

        assertEquals(4, events.size)
        assertEquals("protocol_message", events[1].name)
        assertEquals("binary", events[1].attributes["message.type"])
        assertEquals("binary_frame", events[1].attributes["message.operation"])
        assertEquals("6", events[1].attributes["message.size_bytes"])
        assertEquals("protocol_connection_closed", events[2].name)
        assertEquals("closing", events[2].attributes["close.phase"])
        assertEquals("protocol_connection_closed", events[3].name)
        assertEquals("closed", events[3].attributes["close.phase"])
    }

    @Test
    fun `listener uses custom failure mapper when provided`() {
        val events = mutableListOf<Event>()
        val tracker = tracker(events)
        val telemetry = tracker.okHttpWebSocketTelemetry(
            OkHttpWebSocketTelemetryConfig(
                endpoint = ProtocolEndpoint(name = "presence"),
                connectionStartedOnCreate = false,
                failureMapper = { throwable, _ ->
                    ProtocolFailure(
                        code = "CUSTOM",
                        type = "mapped",
                        message = throwable.message,
                        retryable = false
                    )
                }
            )
        )

        telemetry.listener.onFailure(
            FakeWebSocket,
            IllegalStateException("custom failure"),
            null
        )

        assertEquals(1, events.size)
        assertEquals("protocol_failure", events.single().name)
        assertEquals("CUSTOM", events.single().attributes["error.code"])
        assertEquals("mapped", events.single().attributes["error.type"])
        assertEquals("false", events.single().attributes["error.retryable"])
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
