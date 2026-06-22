package br.com.lab.kotlin.drmanhatan

public data class Protocol(
    public val name: String
) {
    public companion object {
        public val Http: Protocol = Protocol("http")
        public val WebSocket: Protocol = Protocol("websocket")
        public val ServerSentEvents: Protocol = Protocol("sse")
        public val Grpc: Protocol = Protocol("grpc")
        public val Mqtt: Protocol = Protocol("mqtt")
        public val Tcp: Protocol = Protocol("tcp")
        public val Udp: Protocol = Protocol("udp")
    }
}
