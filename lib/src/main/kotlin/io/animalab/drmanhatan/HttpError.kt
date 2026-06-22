package io.animalab.drmanhatan

data class HttpError(
    val code: Int,
    val type: String? = null,
    val message: String? = null
)

