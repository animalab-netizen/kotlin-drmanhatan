package br.com.lab.kotlin.drmanhatan

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
