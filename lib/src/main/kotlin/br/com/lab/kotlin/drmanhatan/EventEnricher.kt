package br.com.lab.kotlin.drmanhatan

public fun interface EventEnricher {
    public fun enrich(event: Event): Event
}
