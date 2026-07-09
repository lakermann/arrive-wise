package ch.lukasakermann.arrivewise.application.service

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.Connection
import ch.lukasakermann.arrivewise.application.domain.ConnectionClass
import ch.lukasakermann.arrivewise.application.domain.Occasion
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

class ClassificationServiceTest {
    private val classificationService = ClassificationService()

    @Test
    fun `returns latest safe connection for client meeting`() {
        val connections =
            listOf(
                createConnection(
                    departure = "12:00",
                    arrival = "12:30",
                ),
                createConnection(
                    departure = "12:30",
                    arrival = "13:00",
                ),
            )

        val result =
            classificationService.findConnection(
                connections = connections,
                targetArrival = createZonedDateTime("13:00"),
                occasion = Occasion.CLIENT_MEETING,
                connectionClass = ConnectionClass.SAFE,
            )

        assertEquals(
            ClassifiedConnection(
                connection =
                    createConnection(
                        departure = "12:00",
                        arrival = "12:30",
                    ),
                connectionClass = ConnectionClass.SAFE,
                minutesBeforeTargetArrival = 30,
            ),
            result,
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("classificationCases")
    fun `classifies connection by arrival buffer`(
        description: String,
        arrival: ZonedDateTime,
        targetArrival: ZonedDateTime,
        requiredBufferMinutes: Int,
        expectedClass: ConnectionClass,
    ) {
        val result =
            classificationService.classifyArrivals(
                arrival = arrival,
                targetArrival = targetArrival,
                requiredBuffer = requiredBufferMinutes.minutes,
            )

        assertEquals(expectedClass, result)
    }

    private fun createConnection(
        departure: String,
        arrival: String,
    ) = Connection(
        from = "Thun",
        to = "Bern",
        departure = createZonedDateTime(departure),
        arrival = createZonedDateTime(arrival),
    )

    companion object {
        @JvmStatic
        fun classificationCases() =
            listOf(
                Arguments.of(
                    "safe when arrival matches required buffer exactly",
                    createZonedDateTime("12:30"),
                    createZonedDateTime("13:00"),
                    30,
                    ConnectionClass.SAFE,
                ),
                Arguments.of(
                    "risky when arrival is inside required buffer",
                    createZonedDateTime("12:31"),
                    createZonedDateTime("13:00"),
                    30,
                    ConnectionClass.RISKY,
                ),
                Arguments.of(
                    "risky when arrival equals target time",
                    createZonedDateTime("13:00"),
                    createZonedDateTime("13:00"),
                    30,
                    ConnectionClass.RISKY,
                ),
                Arguments.of(
                    "too late when arrival is after target time",
                    createZonedDateTime("13:01"),
                    createZonedDateTime("13:00"),
                    30,
                    ConnectionClass.TOO_LATE,
                ),
            )

        private fun createZonedDateTime(time: String): ZonedDateTime = ZonedDateTime.parse("2026-01-01T$time+01:00[Europe/Zurich]")
    }
}
