package br.com.lab.kotlin.drmanhatan

public class CommonMetadataEnricher(
    private val metadata: CommonMetadata
) : EventEnricher {
    override fun enrich(event: Event): Event = event.withAttributes(metadata.asAttributes())
}
