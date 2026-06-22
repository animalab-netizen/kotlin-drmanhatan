package br.com.lab.kotlin.drmanhatan.okhttp

import br.com.lab.kotlin.drmanhatan.ProtocolEndpoint
import br.com.lab.kotlin.drmanhatan.ProtocolFailure
import okhttp3.Response
import okio.ByteString

public data class OkHttpWebSocketTelemetryConfig(
    public val endpoint: ProtocolEndpoint,
    public val sessionId: String? = null,
    public val openAttributes: Map<String, String> = emptyMap(),
    public val closeAttributes: Map<String, String> = emptyMap(),
    public val connectionStartedOnCreate: Boolean = true,
    public val textMessageType: String = "text",
    public val binaryMessageType: String = "binary",
    public val classifyTextOperation: ((String) -> String?)? = null,
    public val classifyBinaryOperation: ((ByteString) -> String?)? = null,
    public val failureMapper: ((Throwable, Response?) -> ProtocolFailure)? = null
)
