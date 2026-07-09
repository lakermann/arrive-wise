package ch.lukasakermann.arrivewise.adapter.connections

import ch.lukasakermann.arrivewise.application.domain.Connection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ConnectionMapperTest {
    private val clock =
        Clock.fixed(
            Instant.parse("2026-01-01T00:00:00Z"),
            ZoneId.of("Europe/Zurich"),
        )

    private val mapper = ConnectionMapper(clock)

    @Test
    fun `maps connection response to domain connection in clock zone`() {
        val response =
            ConnectionsResponse(
                connections = listOf(createConnectionResponse()),
                from = TransportStationResponse("Thun"),
                to = TransportStationResponse("Bern"),
            )

        val result = mapper.toConnections(response)

        assertEquals(
            listOf(
                Connection(
                    from = "Thun",
                    to = "Bern",
                    departure = ZonedDateTime.parse("2026-01-01T13:00+01:00[Europe/Zurich]"),
                    arrival = ZonedDateTime.parse("2026-01-01T13:30+01:00[Europe/Zurich]"),
                ),
            ),
            result,
        )
    }

    private fun createConnectionResponse(
        from: String = "Thun",
        to: String = "Bern",
        departure: String = "2026-01-01T12:00:00Z",
        arrival: String = "2026-01-01T12:30:00Z",
    ) = ConnectionResponse(
        from =
            TransportStopResponse(
                station = TransportStationResponse(from),
                arrival = null,
                departure = OffsetDateTime.parse(departure),
                arrivalTimestamp = null,
                departureTimestamp = 0,
            ),
        to =
            TransportStopResponse(
                station = TransportStationResponse(to),
                arrival = OffsetDateTime.parse(arrival),
                departure = null,
                arrivalTimestamp = 0,
                departureTimestamp = 0,
            ),
        duration = "00d00:30:00",
    )
}
