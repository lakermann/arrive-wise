package ch.lukasakermann.arrivewise.adapter.connections

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConnectionsResponse(
    val connections: List<ConnectionResponse> = emptyList(),
    val from: TransportStationResponse? = null,
    val to: TransportStationResponse? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ConnectionResponse(
    val from: TransportStopResponse,
    val to: TransportStopResponse,
    val duration: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TransportStopResponse(
    val station: TransportStationResponse,
    val arrival: OffsetDateTime? = null,
    val departure: OffsetDateTime? = null,
    val arrivalTimestamp: Long? = null,
    val departureTimestamp: Long? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TransportStationResponse(
    val name: String,
)
