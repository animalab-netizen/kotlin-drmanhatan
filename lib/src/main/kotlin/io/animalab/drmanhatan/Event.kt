package io.animalab.drmanhatan

data class Event(
    val name: String,
    val attributes: Map<String, String> = emptyMap()
) {
    fun withAttribute(key: String, value: String): Event =
        copy(attributes = attributes + (key to value))

    fun withAttributes(values: Map<String, String>): Event =
        copy(attributes = attributes + values)
}

