package br.com.lab.kotlin.drmanhatan

public data class ProtocolClose(
    public val code: Int? = null,
    public val reason: String? = null,
    public val graceful: Boolean? = null,
    public val attributes: Map<String, String> = emptyMap()
) {
    public fun asAttributes(): Map<String, String> = buildMap {
        code?.let { put("close.code", it.toString()) }
        reason?.let { put("close.reason", it) }
        graceful?.let { put("close.graceful", it.toString()) }
        putAll(attributes)
    }
}
