package br.com.lab.kotlin.drmanhatan

public data class HttpError(
    public val code: Int,
    public val type: String? = null,
    public val message: String? = null
)
