package br.com.lab.kotlin.drmanhatan

import java.util.concurrent.CopyOnWriteArrayList

public class DefaultEventBus(
    private val enrichers: List<EventEnricher> = emptyList(),
    private val onObserverError: ((observer: EventObserver, event: Event, error: Throwable) -> Unit)? = null
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
            try {
                observer.onEvent(enrichedEvent)
            } catch (error: Throwable) {
                onObserverError?.invoke(observer, enrichedEvent, error)
            }
        }
    }
}
