package io.animalab.drmanhatan

class CommonMetadataEnricher(
    private val metadata: CommonMetadata
) : EventEnricher {
    override fun enrich(event: Event): Event = event.withAttributes(metadata.asAttributes())
}

