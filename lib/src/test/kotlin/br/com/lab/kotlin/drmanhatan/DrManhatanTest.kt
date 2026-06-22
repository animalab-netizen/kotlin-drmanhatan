package br.com.lab.kotlin.drmanhatan

import kotlin.test.Test
import kotlin.test.assertEquals

class DrManhatanTest {
    @Test
    fun `publishes factory generated event through bus`() {
        val events = mutableListOf<Event>()
        val bus = DefaultEventBus()
        bus.subscribe(EventObserver { events += it })

        val tracker = DrManhatan(
            bus = bus,
            factory = EventFactory(
                metadata = CommonMetadata(appVersion = "1.0.0")
            )
        )

        tracker.tap("Home", "OpenProfile")

        assertEquals(1, events.size)
        assertEquals("tap", events.single().name)
        assertEquals("Home", events.single().attributes["screen.name"])
        assertEquals("OpenProfile", events.single().attributes["event.action"])
        assertEquals("1.0.0", events.single().attributes["app.version"])
    }

    @Test
    fun `publishes websocket lifecycle event`() {
        val events = mutableListOf<Event>()
        val bus = DefaultEventBus()
        bus.subscribe(EventObserver { events += it })

        val tracker = DrManhatan(
            bus = bus,
            factory = EventFactory(
                metadata = CommonMetadata(appVersion = "1.0.0")
            )
        )

        tracker.webSocketConnectionOpened(
            endpoint = ProtocolEndpoint(
                name = "presence",
                address = "wss://presence.example.com",
                channel = "users/live"
            ),
            sessionId = "ws-1"
        )

        assertEquals(1, events.size)
        assertEquals("protocol_connection_opened", events.single().name)
        assertEquals("websocket", events.single().attributes["protocol.name"])
        assertEquals("presence", events.single().attributes["endpoint.name"])
        assertEquals("ws-1", events.single().attributes["session.id"])
        assertEquals("1.0.0", events.single().attributes["app.version"])
    }

    @Test
    fun `tracks a websocket session timeline`() {
        val events = mutableListOf<Event>()
        val bus = DefaultEventBus()
        bus.subscribe(EventObserver { events += it })

        val tracker = DrManhatan(
            bus = bus,
            factory = EventFactory(
                metadata = CommonMetadata(appVersion = "1.0.0")
            )
        )

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
        session.reconnectScheduled(attempt = 2, delayMillis = 2000, reason = "network_lost")
        session.closed(ProtocolClose(code = 1001, reason = "going away", graceful = false))

        assertEquals(6, events.size)
        assertEquals("protocol_connection_started", events[0].name)
        assertEquals("protocol_connection_opened", events[1].name)
        assertEquals("protocol_message", events[2].name)
        assertEquals("outbound", events[2].attributes["message.direction"])
        assertEquals("protocol_message", events[3].name)
        assertEquals("heartbeat", events[3].attributes["message.operation"])
        assertEquals("protocol_reconnect_scheduled", events[4].name)
        assertEquals("2", events[4].attributes["reconnect.attempt"])
        assertEquals("protocol_connection_closed", events[5].name)
        assertEquals("1001", events[5].attributes["close.code"])
    }
}
