package io.animalab.drmanhatan.okhttp

import io.animalab.drmanhatan.ProtocolEndpoint
import io.animalab.drmanhatan.ProtocolFailure
import okhttp3.Response
import okio.ByteString

data class OkHttpWebSocketTelemetryConfig(
    val endpoint: ProtocolEndpoint,
    val sessionId: String? = null,
    val openAttributes: Map<String, String> = emptyMap(),
    val closeAttributes: Map<String, String> = emptyMap(),
    val connectionStartedOnCreate: Boolean = true,
    val textMessageType: String = "text",
    val binaryMessageType: String = "binary",
    val classifyTextOperation: ((String) -> String?)? = null,
    val classifyBinaryOperation: ((ByteString) -> String?)? = null,
    val failureMapper: ((Throwable, Response?) -> ProtocolFailure)? = null
)

