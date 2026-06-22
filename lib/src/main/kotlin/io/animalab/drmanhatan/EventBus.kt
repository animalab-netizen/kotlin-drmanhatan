package io.animalab.drmanhatan

interface EventBus {
    fun subscribe(observer: EventObserver)

    fun unsubscribe(observer: EventObserver)

    fun publish(event: Event)
}

