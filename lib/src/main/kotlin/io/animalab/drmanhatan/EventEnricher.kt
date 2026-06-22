package io.animalab.drmanhatan

fun interface EventEnricher {
    fun enrich(event: Event): Event
}

