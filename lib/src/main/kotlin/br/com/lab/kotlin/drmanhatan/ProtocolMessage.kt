package br.com.lab.kotlin.drmanhatan

data class ProtocolMessage(
    val direction: ProtocolMessageDirection,
    val operation: String? = null,
    val type: String? = null,
    val correlationId: String? = null,
    val sizeBytes: Long? = null,
    val attributes: Map<String, String> = emptyMap()
) {
    fun asAttributes(): Map<String, String> = buildMap {
        put("message.direction", direction.name.lowercase())
        operation?.let { put("message.operation", it) }
        type?.let { put("message.type", it) }
        correlationId?.let { put("message.correlation_id", it) }
        sizeBytes?.let { put("message.size_bytes", it.toString()) }
        putAll(attributes)
    }
}
