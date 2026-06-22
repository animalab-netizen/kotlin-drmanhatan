package io.animalab.drmanhatan

data class CommonMetadata(
    val appVersion: String,
    val platform: String? = null,
    val environment: String? = null,
    val extra: Map<String, String> = emptyMap()
) {
    fun asAttributes(): Map<String, String> = buildMap {
        put("app.version", appVersion)
        platform?.let { put("platform", it) }
        environment?.let { put("environment", it) }
        putAll(extra)
    }
}

