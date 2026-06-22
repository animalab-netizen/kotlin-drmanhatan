package br.com.lab.kotlin.drmanhatan

fun interface EventEnricher {
    fun enrich(event: Event): Event
}
