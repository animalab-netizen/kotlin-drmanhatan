package io.animalab.drmanhatan

data class ProtocolFailure(
    val code: String? = null,
    val type: String? = null,
    val message: String? = null,
    val retryable: Boolean? = null,
    val attributes: Map<String, String> = emptyMap()
) {
    fun asAttributes(): Map<String, String> = buildMap {
        code?.let { put("error.code", it) }
        type?.let { put("error.type", it) }
        message?.let { put("error.message", it) }
        retryable?.let { put("error.retryable", it.toString()) }
        putAll(attributes)
    }
}

