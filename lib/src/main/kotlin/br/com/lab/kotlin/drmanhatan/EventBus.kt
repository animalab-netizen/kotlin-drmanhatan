package br.com.lab.kotlin.drmanhatan

interface EventBus {
    fun subscribe(observer: EventObserver)

    fun unsubscribe(observer: EventObserver)

    fun publish(event: Event)
}
