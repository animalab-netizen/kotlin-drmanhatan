package br.com.lab.kotlin.drmanhatan

public data class ProtocolMessage(
    public val direction: ProtocolMessageDirection,
    public val operation: String? = null,
    public val type: String? = null,
    public val correlationId: String? = null,
    public val sizeBytes: Long? = null,
    public val attributes: Map<String, String> = emptyMap()
) {
    public fun asAttributes(): Map<String, String> = buildMap {
        put("message.direction", direction.name.lowercase())
        operation?.let { put("message.operation", it) }
        type?.let { put("message.type", it) }
        correlationId?.let { put("message.correlation_id", it) }
        sizeBytes?.let { put("message.size_bytes", it.toString()) }
        putAll(attributes)
    }
}
