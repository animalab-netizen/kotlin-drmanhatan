package io.animalab.drmanhatan

class WebSocketSessionTracker(
    drManhatan: DrManhatan,
    endpoint: ProtocolEndpoint,
    sessionId: String? = null
) : ProtocolSessionTracker(
    drManhatan = drManhatan,
    protocol = Protocol.WebSocket,
    endpoint = endpoint,
    sessionId = sessionId
)

