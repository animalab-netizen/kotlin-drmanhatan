package br.com.lab.kotlin.drmanhatan

public interface EventBus {
    public fun subscribe(observer: EventObserver)

    public fun unsubscribe(observer: EventObserver)

    public fun publish(event: Event)
}
