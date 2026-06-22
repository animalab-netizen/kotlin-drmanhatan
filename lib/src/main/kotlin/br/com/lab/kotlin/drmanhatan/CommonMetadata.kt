package br.com.lab.kotlin.drmanhatan

public data class CommonMetadata(
    public val appVersion: String,
    public val platform: String? = null,
    public val environment: String? = null,
    public val extra: Map<String, String> = emptyMap()
) {
    public fun asAttributes(): Map<String, String> = buildMap {
        put("app.version", appVersion)
        platform?.let { put("platform", it) }
        environment?.let { put("environment", it) }
        putAll(extra)
    }
}
