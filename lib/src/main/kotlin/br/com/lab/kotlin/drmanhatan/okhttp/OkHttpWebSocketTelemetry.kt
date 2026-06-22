package br.com.lab.kotlin.drmanhatan.okhttp

import br.com.lab.kotlin.drmanhatan.DrManhatan
import br.com.lab.kotlin.drmanhatan.ProtocolClose
import br.com.lab.kotlin.drmanhatan.ProtocolFailure
import br.com.lab.kotlin.drmanhatan.WebSocketSessionTracker
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.EOFException
import java.net.ProtocolException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class OkHttpWebSocketTelemetry internal constructor(
    val session: WebSocketSessionTracker,
    config: OkHttpWebSocketTelemetryConfig
) {
    private val listenerImpl = Listener(session, config)

    val listener: WebSocketListener = listenerImpl

    fun connectionStarted(attributes: Map<String, String> = emptyMap()) {
        session.connectionStarted(attributes)
    }

    private class Listener(
        private val session: WebSocketSessionTracker,
        private val config: OkHttpWebSocketTelemetryConfig
    ) : WebSocketListener() {
        init {
            if (config.connectionStartedOnCreate) {
                session.connectionStarted()
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            session.connectionOpened(
                config.openAttributes + mapOf(
                    "handshake.code" to response.code.toString()
                )
            )
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            session.inboundMessage(
                operation = config.classifyTextOperation?.invoke(text),
                type = config.textMessageType,
                sizeBytes = text.toByteArray(Charsets.UTF_8).size.toLong()
            )
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            session.inboundMessage(
                operation = config.classifyBinaryOperation?.invoke(bytes),
                type = config.binaryMessageType,
                sizeBytes = bytes.size.toLong()
            )
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            session.closed(
                ProtocolClose(
                    code = code,
                    reason = reason,
                    graceful = true,
                    attributes = config.closeAttributes + mapOf("close.phase" to "closing")
                )
            )
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            session.closed(
                ProtocolClose(
                    code = code,
                    reason = reason,
                    graceful = true,
                    attributes = config.closeAttributes + mapOf("close.phase" to "closed")
                )
            )
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            session.failure(config.failureMapper?.invoke(t, response) ?: defaultFailure(t, response))
        }

        private fun defaultFailure(t: Throwable, response: Response?): ProtocolFailure {
            val responseCode = response?.code?.toString()
            val responseMessage = response?.message

            return ProtocolFailure(
                code = responseCode,
                type = classifyThrowable(t),
                message = t.message ?: responseMessage ?: "websocket failure",
                retryable = isRetryable(t, response),
                attributes = buildMap {
                    responseCode?.let { put("handshake.code", it) }
                    responseMessage?.takeIf { it.isNotBlank() }?.let { put("handshake.message", it) }
                    put("error.exception", t.javaClass.name)
                }
            )
        }

        private fun classifyThrowable(t: Throwable): String = when (t) {
            is SocketTimeoutException -> "timeout"
            is EOFException -> "connection_closed"
            is UnknownHostException -> "dns"
            is ProtocolException -> "protocol"
            else -> "transport"
        }

        private fun isRetryable(t: Throwable, response: Response?): Boolean {
            if (t is UnknownHostException || t is SocketTimeoutException || t is EOFException) {
                return true
            }

            val code = response?.code ?: return false
            return code in 500..599 || code == 429
        }
    }
}

fun DrManhatan.okHttpWebSocketTelemetry(
    config: OkHttpWebSocketTelemetryConfig
): OkHttpWebSocketTelemetry = OkHttpWebSocketTelemetry(
    session = webSocketSession(
        endpoint = config.endpoint,
        sessionId = config.sessionId
    ),
    config = config
)
