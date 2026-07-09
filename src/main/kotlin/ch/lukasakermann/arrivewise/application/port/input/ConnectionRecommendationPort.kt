package ch.lukasakermann.arrivewise.application.port.input

import ch.lukasakermann.arrivewise.application.domain.ConnectionRecommendation

interface ConnectionRecommendationPort {
    fun getConnectionRecommendation(question: String): ConnectionRecommendation
}
