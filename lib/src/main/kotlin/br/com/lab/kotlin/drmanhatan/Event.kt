package br.com.lab.kotlin.drmanhatan

public data class Event(
    public val name: String,
    public val attributes: Map<String, String> = emptyMap()
) {
    public fun withAttribute(key: String, value: String): Event =
        copy(attributes = attributes + (key to value))

    public fun withAttributes(values: Map<String, String>): Event =
        copy(attributes = attributes + values)
}
