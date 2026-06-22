package br.com.lab.kotlin.drmanhatan

import java.util.concurrent.CopyOnWriteArrayList

class DefaultEventBus(
    private val enrichers: List<EventEnricher> = emptyList()
) : EventBus {
    private val observers = CopyOnWriteArrayList<EventObserver>()

    override fun subscribe(observer: EventObserver) {
        observers += observer
    }

    override fun unsubscribe(observer: EventObserver) {
        observers -= observer
    }

    override fun publish(event: Event) {
        val enrichedEvent = enrichers.fold(event) { current, enricher ->
            enricher.enrich(current)
        }

        observers.forEach { observer ->
            observer.onEvent(enrichedEvent)
        }
    }
}
