package br.com.lab.kotlin.drmanhatan

data class HttpError(
    val code: Int,
    val type: String? = null,
    val message: String? = null
)
