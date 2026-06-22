package br.com.lab.kotlin.drmanhatan

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultEventBusTest {
    @Test
    fun `publishes enriched events to observers`() {
        val received = mutableListOf<Event>()
        val observer = EventObserver { received += it }
        val bus = DefaultEventBus(
            enrichers = listOf(
                CommonMetadataEnricher(
                    CommonMetadata(
                        appVersion = "1.2.3",
                        platform = "android"
                    )
                )
            )
        )

        bus.subscribe(observer)
        bus.publish(Event(name = "screen_viewed", attributes = mapOf("screen.name" to "Home")))

        assertEquals(1, received.size)
        assertEquals("screen_viewed", received.first().name)
        assertEquals("1.2.3", received.first().attributes["app.version"])
        assertEquals("android", received.first().attributes["platform"])
        assertEquals("Home", received.first().attributes["screen.name"])
    }

    @Test
    fun `unsubscribe stops delivery`() {
        var deliveries = 0
        val observer = EventObserver { deliveries++ }
        val bus = DefaultEventBus()

        bus.subscribe(observer)
        bus.unsubscribe(observer)
        bus.publish(Event(name = "tap"))

        assertEquals(0, deliveries)
    }

    @Test
    fun `publishes to observers in subscription order`() {
        val deliveryOrder = mutableListOf<String>()
        val bus = DefaultEventBus()

        bus.subscribe(EventObserver { deliveryOrder += "first" })
        bus.subscribe(EventObserver { deliveryOrder += "second" })

        bus.publish(Event(name = "ordered_event"))

        assertEquals(listOf("first", "second"), deliveryOrder)
    }

    @Test
    fun `applies multiple enrichers in sequence`() {
        val received = mutableListOf<Event>()
        val bus = DefaultEventBus(
            enrichers = listOf(
                EventEnricher { event -> event.withAttribute("first", "1") },
                EventEnricher { event -> event.withAttribute("second", "2") }
            )
        )

        bus.subscribe(EventObserver { received += it })
        bus.publish(Event(name = "custom"))

        assertEquals("1", received.single().attributes["first"])
        assertEquals("2", received.single().attributes["second"])
    }

    @Test
    fun `continues delivery when an observer fails`() {
        val received = mutableListOf<String>()
        val errors = mutableListOf<Throwable>()
        val bus = DefaultEventBus(
            onObserverError = { _, _, error -> errors += error }
        )

        bus.subscribe(EventObserver { throw IllegalStateException("observer failure") })
        bus.subscribe(EventObserver { received += it.name })

        bus.publish(Event(name = "resilient_event"))

        assertEquals(listOf("resilient_event"), received)
        assertEquals(1, errors.size)
        assertIs<IllegalStateException>(errors.single())
    }
}
