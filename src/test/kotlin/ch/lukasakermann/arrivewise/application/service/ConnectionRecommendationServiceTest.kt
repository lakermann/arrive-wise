package ch.lukasakermann.arrivewise.application.service

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.Connection
import ch.lukasakermann.arrivewise.application.domain.ConnectionClass
import ch.lukasakermann.arrivewise.application.domain.ConnectionRecommendation
import ch.lukasakermann.arrivewise.application.domain.Occasion
import ch.lukasakermann.arrivewise.application.domain.TravelIntent
import ch.lukasakermann.arrivewise.application.port.output.ChatPort
import ch.lukasakermann.arrivewise.application.port.output.ConnectionSearchPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.ZonedDateTime

class ConnectionRecommendationServiceTest {
    private val chatPort = mock<ChatPort>()
    private val connectionSearchPort = mock<ConnectionSearchPort>()

    private val service =
        ConnectionRecommendationService(
            chatPort = chatPort,
            connectionSearchPort = connectionSearchPort,
        )

    @Test
    fun `builds recommendation from extracted intent and searched connections`() {
        val question = "My question"
        val targetArrival = generateZonedDateTime("13:00")

        val travelIntent =
            TravelIntent(
                from = "Thun",
                to = "Bern",
                targetArrival = targetArrival,
                occasion = Occasion.CLIENT_MEETING,
            )

        val safeConnection =
            createConnection(
                departure = "12:00",
                arrival = "12:30",
            )
        val riskyConnection =
            createConnection(
                departure = "12:30",
                arrival = "13:00",
            )

        val latestSafeConnection =
            ClassifiedConnection(
                connection = safeConnection,
                minutesBeforeTargetArrival = 30,
                connectionClass = ConnectionClass.SAFE,
            )
        val latestPossibleConnection =
            ClassifiedConnection(
                connection = riskyConnection,
                minutesBeforeTargetArrival = 0,
                connectionClass = ConnectionClass.RISKY,
            )

        whenever(chatPort.extractTravelIntent(question))
            .thenReturn(travelIntent)

        whenever(
            connectionSearchPort.connections(
                from = "Thun",
                to = "Bern",
                date = targetArrival.toLocalDate(),
                time = targetArrival.toLocalTime(),
            ),
        ).thenReturn(
            listOf(safeConnection, riskyConnection),
        )

        whenever(
            chatPort.generateConnectionRecommendation(
                question = question,
                travelIntent = travelIntent,
                latestSafeConnection = latestSafeConnection,
                latestPossibleConnection = latestPossibleConnection,
            ),
        ).thenReturn("My recommendation")

        val result = service.getConnectionRecommendation(question)

        assertEquals(
            ConnectionRecommendation(
                targetArrival = targetArrival,
                latestSafeConnection = latestSafeConnection,
                latestPossibleConnection = latestPossibleConnection,
                recommendation = "My recommendation",
            ),
            result,
        )

        verify(chatPort).extractTravelIntent(question)
        verify(connectionSearchPort).connections(
            from = "Thun",
            to = "Bern",
            date = targetArrival.toLocalDate(),
            time = targetArrival.toLocalTime(),
        )
        verify(chatPort).generateConnectionRecommendation(
            question = question,
            travelIntent = travelIntent,
            latestSafeConnection = latestSafeConnection,
            latestPossibleConnection = latestPossibleConnection,
        )
    }

    private fun createConnection(
        departure: String,
        arrival: String,
    ) = Connection(
        from = "Thun",
        to = "Bern",
        departure = generateZonedDateTime(departure),
        arrival = generateZonedDateTime(arrival),
    )

    private fun generateZonedDateTime(time: String): ZonedDateTime = ZonedDateTime.parse("2026-01-01T$time+01:00[Europe/Zurich]")
}
