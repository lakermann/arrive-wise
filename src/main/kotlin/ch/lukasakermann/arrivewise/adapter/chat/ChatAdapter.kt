package ch.lukasakermann.arrivewise.adapter.chat

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.TravelIntent
import ch.lukasakermann.arrivewise.application.port.output.ChatPort
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component

@Component
class ChatAdapter(
    private val chatClient: ChatClient,
    private val travelIntentMapper: TravelIntentMapper,
) : ChatPort {
    private val log = LoggerFactory.getLogger(ChatAdapter::class.java)

    override fun extractTravelIntent(question: String): TravelIntent {
        log.info("Received chat question: {}", question)

        val travelIntentResponse =
            chatClient
                .prompt()
                .system(TRAVEL_INTENT_SYSTEM_PROMPT)
                .user(question)
                .call()
                .entity(TravelIntentResponse::class.java)
                ?: throw RuntimeException("Model returned no travel intent response")

        log.info("Extracted travel intent: {}", travelIntentResponse)

        return travelIntentMapper.toTravelIntent(travelIntentResponse)
    }

    override fun generateConnectionRecommendation(
        question: String,
        travelIntent: TravelIntent,
        latestSafeConnection: ClassifiedConnection?,
        latestPossibleConnection: ClassifiedConnection?,
    ): String {
        log.info(
            "Generating connection recommendation for travelIntent={}, latestSafeConnection={}, latestPossibleConnection={}",
            travelIntent,
            latestSafeConnection,
            latestPossibleConnection,
        )

        val connectionRecommendation =
            chatClient
                .prompt()
                .system(CONNECTION_RECOMMENDATION_SYSTEM_PROMPT)
                .user(
                    """
                    User question:
                    $question

                    Extracted travel intent:
                    $travelIntent

                    Latest safe connection:
                    $latestSafeConnection

                    Latest possible connection:
                    $latestPossibleConnection

                    """.trimIndent(),
                ).call()
                .content()
                ?: throw RuntimeException("Model returned no connection recommendation response")

        log.info("Generated connection recommendation: {}", connectionRecommendation)

        return connectionRecommendation
    }

    private companion object {
        const val TRAVEL_INTENT_SYSTEM_PROMPT = """
            You are ArriveWise, a local AI CLI assistant for Swiss public transport travel intent extraction.

            Your task is to extract the user's travel intent from their message.
            You do NOT search for train connections.
            You do NOT recommend a connection.
            You do NOT invent departure times, arrival times, platforms, stations, delays, or routes.
        """

        const val CONNECTION_RECOMMENDATION_SYSTEM_PROMPT = """
            You are ArriveWise, a local AI CLI assistant for Swiss public transport travel recommendation.

            Your task is to generate a short explanation for the connection recommendation already shown to the user.
            You receive the user's question, extracted travel intent, latest safe connection, and latest possible connection.
            You do NOT search for connections.
            You do NOT reproduce headings, connections, labels, or formatting.
            You do NOT invent or modify times, stations, routes, delays, platforms, buffers, or other journey details.
            You only use the information provided in the input.
            
            Recommend the latest safe connection by default.
            Mention the latest possible connection only when it is useful to explain the trade-off or warn about an insufficient buffer.
            Base the explanation on the user's travel intent and the provided buffers.
            Avoid repeating details that are already visible unless they are necessary to make the recommendation clear.
            Do not claim that a connection is guaranteed.
            
            Return only 2 or 3 concise plain-text sentences, no line breaks.
            Always use 24-hour time format
            Do not use a title, bullets, markdown, labels, or a preamble.
        """
    }
}
