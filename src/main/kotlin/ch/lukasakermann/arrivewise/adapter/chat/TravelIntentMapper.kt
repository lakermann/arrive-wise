package ch.lukasakermann.arrivewise.adapter.chat

import ch.lukasakermann.arrivewise.application.domain.Occasion
import ch.lukasakermann.arrivewise.application.domain.TravelIntent
import ch.lukasakermann.arrivewise.application.domain.TravelIntentException
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class TravelIntentMapper(
    private val clock: Clock,
) {
    fun toTravelIntent(response: TravelIntentResponse): TravelIntent {
        val resolvedFrom =
            requireNotNull(
                response.from?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw TravelIntentException("Missing required field: from", "departure location"),
            )
        val resolvedTo =
            requireNotNull(
                response.to?.trim()?.takeIf { it.isNotEmpty() }
                    ?: throw TravelIntentException("Missing required field: to", "destination"),
            )

        val resolvedDate =
            response.date
                ?.trim()
                ?.takeIf(String::isNotEmpty)
                ?.let(LocalDate::parse)
                ?: LocalDate.now(clock)

        val resolvedTargetArrivalTime =
            response.targetArrivalTime
                ?.trim()
                ?.takeIf(String::isNotEmpty)
                ?.let(LocalTime::parse)
                ?: throw TravelIntentException(message = "Missing required field: targetArrivalTime", "arrival time")

        val localDateTime = LocalDateTime.of(resolvedDate, resolvedTargetArrivalTime)

        return TravelIntent(
            from = resolvedFrom,
            to = resolvedTo,
            targetArrival = localDateTime.atZone(clock.zone),
            occasion = response.occasion.toDomain(),
        )
    }

    private fun OccasionResponse?.toDomain(): Occasion =
        when (this) {
            OccasionResponse.CLIENT_MEETING -> Occasion.CLIENT_MEETING
            OccasionResponse.WORKSHOP -> Occasion.WORKSHOP
            OccasionResponse.INTERVIEW -> Occasion.INTERVIEW
            OccasionResponse.INTERNAL_MEETING -> Occasion.INTERNAL_MEETING
            OccasionResponse.CASUAL -> Occasion.CASUAL
            OccasionResponse.UNKNOWN, null -> Occasion.UNKNOWN
        }
}
