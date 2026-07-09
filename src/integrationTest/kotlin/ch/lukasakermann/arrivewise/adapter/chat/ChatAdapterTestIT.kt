package ch.lukasakermann.arrivewise.adapter.chat

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.Connection
import ch.lukasakermann.arrivewise.application.domain.ConnectionClass
import ch.lukasakermann.arrivewise.application.domain.Occasion
import ch.lukasakermann.arrivewise.application.domain.TravelIntent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

@SpringBootTest
@ActiveProfiles("test")
class ChatAdapterTestIT {
    @Autowired
    private lateinit var chatAdapter: ChatAdapter

    private val zone = ZoneId.of("Europe/Zurich")
    private val date = LocalDate.of(2026, 1, 2)

    @Test
    fun `extracts client meeting travel intent`() {
        val result =
            chatAdapter.extractTravelIntent(
                "I'm in Zürich and have a client meeting in Bern on $date at 09:00. What is the latest safe train?",
            )

        assertEquals(
            TravelIntent(
                from = "Zürich",
                to = "Bern",
                targetArrival = date.atTime(9, 0).atZone(zone),
                occasion = Occasion.CLIENT_MEETING,
            ),
            result,
        )
    }

    @Test
    fun `generates recommendation without recommendation prefix`() {
        val targetArrival = ZonedDateTime.parse("2026-01-01T13:00+01:00[Europe/Zurich]")

        val result =
            chatAdapter.generateConnectionRecommendation(
                question = "I'm in Thun and need to be in Bern by 13:00 for a client meeting",
                travelIntent =
                    TravelIntent(
                        from = "Thun",
                        to = "Bern",
                        targetArrival = targetArrival,
                        occasion = Occasion.CLIENT_MEETING,
                    ),
                latestSafeConnection =
                    createClassifiedConnection(
                        departure = "2026-01-01T12:00+01:00[Europe/Zurich]",
                        arrival = "2026-01-01T12:30+01:00[Europe/Zurich]",
                        minutesBeforeTargetArrival = 30,
                        connectionClass = ConnectionClass.SAFE,
                    ),
                latestPossibleConnection =
                    createClassifiedConnection(
                        departure = "2026-01-01T12:30+01:00[Europe/Zurich]",
                        arrival = "2026-01-01T13:00+01:00[Europe/Zurich]",
                        minutesBeforeTargetArrival = 0,
                        connectionClass = ConnectionClass.RISKY,
                    ),
            )

        assertNotNull(result)
        assertFalse(result.contains("Recommendation:"))
        assertTrue(result.contains("12:00") || result.contains("12:30"))
    }

    private fun createClassifiedConnection(
        departure: String,
        arrival: String,
        minutesBeforeTargetArrival: Long,
        connectionClass: ConnectionClass,
    ) = ClassifiedConnection(
        connection =
            Connection(
                from = "Thun",
                to = "Bern",
                departure = ZonedDateTime.parse(departure),
                arrival = ZonedDateTime.parse(arrival),
            ),
        minutesBeforeTargetArrival = minutesBeforeTargetArrival,
        connectionClass = connectionClass,
    )
}
