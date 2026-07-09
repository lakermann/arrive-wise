package ch.lukasakermann.arrivewise.application.service

import ch.lukasakermann.arrivewise.application.domain.ConnectionClass
import ch.lukasakermann.arrivewise.application.domain.ConnectionRecommendation
import ch.lukasakermann.arrivewise.application.port.input.ConnectionRecommendationPort
import ch.lukasakermann.arrivewise.application.port.output.ChatPort
import ch.lukasakermann.arrivewise.application.port.output.ConnectionSearchPort

class ConnectionRecommendationService(
    private val chatPort: ChatPort,
    private val connectionSearchPort: ConnectionSearchPort,
) : ConnectionRecommendationPort {
    private val classificationService = ClassificationService()

    override fun getConnectionRecommendation(question: String): ConnectionRecommendation {
        val travelIntent = chatPort.extractTravelIntent(question)
        val connections =
            connectionSearchPort.connections(
                travelIntent.from,
                travelIntent.to,
                travelIntent.targetArrival.toLocalDate(),
                travelIntent.targetArrival.toLocalTime(),
            )

        val latestSafeConnection =
            classificationService.findConnection(
                connections = connections,
                targetArrival = travelIntent.targetArrival,
                occasion = travelIntent.occasion,
                connectionClass = ConnectionClass.SAFE,
            )

        val latestPossibleConnection =
            classificationService.findConnection(
                connections = connections,
                targetArrival = travelIntent.targetArrival,
                occasion = travelIntent.occasion,
                connectionClass = ConnectionClass.RISKY,
            )

        val recommendation =
            chatPort.generateConnectionRecommendation(
                question = question,
                travelIntent = travelIntent,
                latestSafeConnection = latestSafeConnection,
                latestPossibleConnection = latestPossibleConnection,
            )

        return ConnectionRecommendation(
            targetArrival = travelIntent.targetArrival,
            latestSafeConnection = latestSafeConnection,
            latestPossibleConnection = latestPossibleConnection,
            recommendation = recommendation,
        )
    }
}
