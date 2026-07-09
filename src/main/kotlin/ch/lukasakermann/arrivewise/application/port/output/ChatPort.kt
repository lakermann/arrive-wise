package ch.lukasakermann.arrivewise.application.port.output

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.TravelIntent

interface ChatPort {
    fun extractTravelIntent(question: String): TravelIntent

    fun generateConnectionRecommendation(
        question: String,
        travelIntent: TravelIntent,
        latestSafeConnection: ClassifiedConnection?,
        latestPossibleConnection: ClassifiedConnection?,
    ): String
}
