package ch.lukasakermann.arrivewise.adapter.cli

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.Connection
import ch.lukasakermann.arrivewise.application.domain.ConnectionClass
import ch.lukasakermann.arrivewise.application.domain.ConnectionRecommendation
import ch.lukasakermann.arrivewise.application.domain.TravelIntentException
import ch.lukasakermann.arrivewise.application.port.input.ConnectionRecommendationPort
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.io.PrintWriter
import java.io.StringWriter
import java.time.ZonedDateTime
import kotlin.test.assertTrue

class CliAdapterTest {
    @Test
    fun `prints recommendation and exits when exit is entered`() {
        val connectionRecommendationPort = mock<ConnectionRecommendationPort>()
        val question = "I'm in Thun and need to be in Bern by 13:00 for a client meeting"

        whenever(connectionRecommendationPort.getConnectionRecommendation(question))
            .thenReturn(createConnectionRecommendation())

        val output = runCli(connectionRecommendationPort, question)

        assertTrue(output.contains("""Describe your trip, or type "exit" to quit."""), output)
        assertTrue(output.contains("> "), output)
        assertTrue(
            output.contains(
                """
                ✓ Recommended
                  12:00 Thun → 12:30 Bern
                  30 min buffer

                ⚠ Latest feasible
                  12:30 Thun → 13:00 Bern
                  0 min buffer

                My recommendation
                """.trimIndent(),
            ),
            output,
        )

        verify(connectionRecommendationPort).getConnectionRecommendation(question)
        verifyNoMoreInteractions(connectionRecommendationPort)
    }

    @Test
    fun `prints no connection found when no connections are available`() {
        val connectionRecommendationPort = mock<ConnectionRecommendationPort>()
        val question = "I'm in Thun and need to be in Bern by 13:00 on 1900-01-01 for a client meeting"

        whenever(connectionRecommendationPort.getConnectionRecommendation(question))
            .thenReturn(
                createConnectionRecommendation(
                    latestSafeConnection = null,
                    latestPossibleConnection = null,
                ),
            )

        val output = runCli(connectionRecommendationPort, question)

        assertTrue(
            output.contains(
                """
                ✓ Recommended
                  No connection found

                ⚠ Latest feasible
                  No connection found
                """.trimIndent(),
            ),
            output,
        )

        verify(connectionRecommendationPort).getConnectionRecommendation(question)
        verifyNoMoreInteractions(connectionRecommendationPort)
    }

    @Test
    fun `prints missing information when travel intent is invalid`() {
        val connectionRecommendationPort = mock<ConnectionRecommendationPort>()
        val question = "I need to be in Bern by 13:00 for a client meeting"

        whenever(connectionRecommendationPort.getConnectionRecommendation(question))
            .thenThrow(TravelIntentException("Missing required field: from", "departure location"))

        val output = runCli(connectionRecommendationPort, question)

        assertTrue(output.contains("Please provide the following information: departure location"), output)

        verify(connectionRecommendationPort).getConnectionRecommendation(question)
        verifyNoMoreInteractions(connectionRecommendationPort)
    }

    @Test
    fun `prints generic error when recommendation fails`() {
        val connectionRecommendationPort = mock<ConnectionRecommendationPort>()
        val question = "Test"

        whenever(connectionRecommendationPort.getConnectionRecommendation(question))
            .thenThrow(RuntimeException("Test exception message"))

        val output = runCli(connectionRecommendationPort, question)

        assertTrue(output.contains("Sorry, something went wrong: Test exception message"), output)

        verify(connectionRecommendationPort).getConnectionRecommendation(question)
        verifyNoMoreInteractions(connectionRecommendationPort)
    }

    private fun runCli(
        connectionRecommendationPort: ConnectionRecommendationPort,
        question: String,
    ): String {
        val input =
            """
            $question
            exit
            """.trimIndent()

        val output = StringWriter()

        CliAdapter(
            connectionRecommendationPort = connectionRecommendationPort,
            reader = input.reader(),
            output = PrintWriter(output, true),
        ).run()

        return output.toString()
    }

    private fun createConnectionRecommendation(
        recommendation: String = "My recommendation",
        latestSafeConnection: ClassifiedConnection? =
            ClassifiedConnection(
                connection =
                    createConnection(
                        departure = "12:00",
                        arrival = "12:30",
                    ),
                minutesBeforeTargetArrival = 30,
                connectionClass = ConnectionClass.SAFE,
            ),
        latestPossibleConnection: ClassifiedConnection? =
            ClassifiedConnection(
                connection =
                    createConnection(
                        departure = "12:30",
                        arrival = "13:00",
                    ),
                minutesBeforeTargetArrival = 0,
                connectionClass = ConnectionClass.RISKY,
            ),
    ) = ConnectionRecommendation(
        targetArrival = createZonedDateTime("13:00"),
        latestSafeConnection = latestSafeConnection,
        latestPossibleConnection = latestPossibleConnection,
        recommendation = recommendation,
    )

    private fun createConnection(
        departure: String,
        arrival: String,
    ) = Connection(
        from = "Thun",
        to = "Bern",
        departure = createZonedDateTime(departure),
        arrival = createZonedDateTime(arrival),
    )

    private fun createZonedDateTime(time: String): ZonedDateTime = ZonedDateTime.parse("2026-01-01T$time+01:00[Europe/Zurich]")
}
