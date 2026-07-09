package ch.lukasakermann.arrivewise.application.domain

import java.time.ZonedDateTime

data class ConnectionRecommendation(
    val targetArrival: ZonedDateTime,
    val latestSafeConnection: ClassifiedConnection?,
    val latestPossibleConnection: ClassifiedConnection?,
    val recommendation: String,
)
