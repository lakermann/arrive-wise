package ch.lukasakermann.arrivewise.adapter.chat

import ch.lukasakermann.arrivewise.application.domain.Occasion
import ch.lukasakermann.arrivewise.application.domain.TravelIntent
import ch.lukasakermann.arrivewise.application.domain.TravelIntentException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class TravelIntentMapperTest {
    private val clock =
        Clock.fixed(
            Instant.parse("2026-01-01T12:00:00Z"),
            ZoneId.of("Europe/Zurich"),
        )

    private val mapper = TravelIntentMapper(clock)

    @Test
    fun `maps response to travel intent`() {
        val response =
            createTravelIntentResponse(
                from = " Thun ",
                to = " Bern ",
                date = " 2026-01-01 ",
                time = " 12:00 ",
                occasion = OccasionResponse.CLIENT_MEETING,
            )

        val result = mapper.toTravelIntent(response)

        assertEquals(
            TravelIntent(
                from = "Thun",
                to = "Bern",
                targetArrival = ZonedDateTime.parse("2026-01-01T12:00+01:00[Europe/Zurich]"),
                occasion = Occasion.CLIENT_MEETING,
            ),
            result,
        )
    }

    @Test
    fun `uses current date and unknown occasion when omitted`() {
        val response =
            createTravelIntentResponse(
                date = "",
                occasion = null,
            )

        val result = mapper.toTravelIntent(response)

        assertEquals(
            TravelIntent(
                from = "Thun",
                to = "Bern",
                targetArrival = ZonedDateTime.parse("2026-01-01T12:00+01:00[Europe/Zurich]"),
                occasion = Occasion.UNKNOWN,
            ),
            result,
        )
    }

    @ParameterizedTest(name = "rejects missing required field: {1}")
    @MethodSource("missingRequiredFields")
    fun `rejects missing required fields`(
        response: TravelIntentResponse,
        expectedField: String,
    ) {
        val exception =
            assertThrows<TravelIntentException> {
                mapper.toTravelIntent(response)
            }

        assertEquals(
            "Missing required field: $expectedField",
            exception.message,
        )
    }

    companion object {
        @JvmStatic
        fun missingRequiredFields(): List<Arguments> =
            listOf(
                Arguments.of(
                    createTravelIntentResponse(from = ""),
                    "from",
                ),
                Arguments.of(
                    createTravelIntentResponse(to = ""),
                    "to",
                ),
                Arguments.of(
                    createTravelIntentResponse(time = ""),
                    "targetArrivalTime",
                ),
            )

        private fun createTravelIntentResponse(
            from: String = "Thun",
            to: String = "Bern",
            date: String = "2026-01-01",
            time: String = "12:00",
            occasion: OccasionResponse? = OccasionResponse.CLIENT_MEETING,
        ) = TravelIntentResponse(
            from = from,
            to = to,
            date = date,
            targetArrivalTime = time,
            occasion = occasion,
        )
    }
}
