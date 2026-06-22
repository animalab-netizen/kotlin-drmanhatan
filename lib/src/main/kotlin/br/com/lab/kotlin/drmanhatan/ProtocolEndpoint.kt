package br.com.lab.kotlin.drmanhatan

data class ProtocolEndpoint(
    val name: String,
    val address: String? = null,
    val channel: String? = null
) {
    fun asAttributes(): Map<String, String> = buildMap {
        put("endpoint.name", name)
        address?.let { put("endpoint.address", it) }
        channel?.let { put("endpoint.channel", it) }
    }
}
