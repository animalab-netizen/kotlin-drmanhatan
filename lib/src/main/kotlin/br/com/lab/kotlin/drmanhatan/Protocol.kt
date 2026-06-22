package br.com.lab.kotlin.drmanhatan

data class Protocol(
    val name: String
) {
    companion object {
        val Http = Protocol("http")
        val WebSocket = Protocol("websocket")
        val ServerSentEvents = Protocol("sse")
        val Grpc = Protocol("grpc")
        val Mqtt = Protocol("mqtt")
        val Tcp = Protocol("tcp")
        val Udp = Protocol("udp")
    }
}
