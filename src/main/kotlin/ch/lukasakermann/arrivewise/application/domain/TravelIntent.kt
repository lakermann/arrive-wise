package ch.lukasakermann.arrivewise.application.domain

import java.time.ZonedDateTime

data class TravelIntent(
    val from: String,
    val to: String,
    val targetArrival: ZonedDateTime,
    val occasion: Occasion,
)

enum class Occasion {
    CLIENT_MEETING,
    WORKSHOP,
    INTERVIEW,
    INTERNAL_MEETING,
    CASUAL,
    UNKNOWN,
}
