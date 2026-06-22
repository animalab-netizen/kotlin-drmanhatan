package br.com.lab.kotlin.drmanhatan

public data class ProtocolFailure(
    public val code: String? = null,
    public val type: String? = null,
    public val message: String? = null,
    public val retryable: Boolean? = null,
    public val attributes: Map<String, String> = emptyMap()
) {
    public fun asAttributes(): Map<String, String> = buildMap {
        code?.let { put("error.code", it) }
        type?.let { put("error.type", it) }
        message?.let { put("error.message", it) }
        retryable?.let { put("error.retryable", it.toString()) }
        putAll(attributes)
    }
}
