package br.com.lab.kotlin.drmanhatan

public data class ProtocolEndpoint(
    public val name: String,
    public val address: String? = null,
    public val channel: String? = null
) {
    public fun asAttributes(): Map<String, String> = buildMap {
        put("endpoint.name", name)
        address?.let { put("endpoint.address", it) }
        channel?.let { put("endpoint.channel", it) }
    }
}
