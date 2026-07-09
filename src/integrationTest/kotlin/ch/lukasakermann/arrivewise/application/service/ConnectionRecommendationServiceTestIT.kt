package ch.lukasakermann.arrivewise.application.service

import ch.lukasakermann.arrivewise.application.port.input.ConnectionRecommendationPort
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ConnectionRecommendationServiceTestIT {
    @Autowired
    private lateinit var connectionRecommendationService: ConnectionRecommendationPort

    @Test
    fun `returns a recommendation for a client meeting`() {
        val recommendation =
            connectionRecommendationService.getConnectionRecommendation(
                "I'm in Thun and have a client meeting in Bern on 2026-01-15 at 09:00.",
            )

        assertEquals("Thun", recommendation.latestSafeConnection?.connection?.from)
        assertEquals("Bern", recommendation.latestSafeConnection?.connection?.to)
        assertEquals("Thun", recommendation.latestPossibleConnection?.connection?.from)
        assertEquals("Bern", recommendation.latestPossibleConnection?.connection?.to)
        assertTrue(recommendation.recommendation.isNotBlank())
    }
}
