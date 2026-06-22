package io.animalab.drmanhatan

data class ProtocolClose(
    val code: Int? = null,
    val reason: String? = null,
    val graceful: Boolean? = null,
    val attributes: Map<String, String> = emptyMap()
) {
    fun asAttributes(): Map<String, String> = buildMap {
        code?.let { put("close.code", it.toString()) }
        reason?.let { put("close.reason", it) }
        graceful?.let { put("close.graceful", it.toString()) }
        putAll(attributes)
    }
}

