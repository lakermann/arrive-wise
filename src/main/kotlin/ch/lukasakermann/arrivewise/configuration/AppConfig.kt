package ch.lukasakermann.arrivewise.configuration

import ch.lukasakermann.arrivewise.adapter.chat.ChatAdapter
import ch.lukasakermann.arrivewise.adapter.chat.TravelIntentMapper
import ch.lukasakermann.arrivewise.adapter.cli.CliAdapter
import ch.lukasakermann.arrivewise.adapter.connections.ConnectionMapper
import ch.lukasakermann.arrivewise.adapter.connections.ConnectionsSearchAdapter
import ch.lukasakermann.arrivewise.application.port.input.ConnectionRecommendationPort
import ch.lukasakermann.arrivewise.application.service.ConnectionRecommendationService
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.model.ChatModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.time.Clock
import java.time.ZoneId

@Configuration
class AppConfig(
    @Value($$"${app.time-zone:Europe/Zurich}")
    private val timeZone: String,
) {
    @Bean
    fun appClock(): Clock = Clock.system(ZoneId.of(timeZone))

    @Bean
    fun chatClient(chatModel: ChatModel): ChatClient = ChatClient.builder(chatModel).build()

    @Bean
    fun travelIntentMapper(clock: Clock): TravelIntentMapper = TravelIntentMapper(clock)

    @Bean
    fun connectionMapper(clock: Clock): ConnectionMapper = ConnectionMapper(clock)

    @Bean
    fun arrivalPort(
        chatClient: ChatClient,
        travelIntentMapper: TravelIntentMapper,
        connectionMapper: ConnectionMapper,
    ): ConnectionRecommendationPort =
        ConnectionRecommendationService(
            ChatAdapter(chatClient, travelIntentMapper),
            ConnectionsSearchAdapter(connectionMapper),
        )

    @Bean
    @Profile("!test")
    fun cliAdapter(connectionRecommendationPort: ConnectionRecommendationPort): CommandLineRunner = CliAdapter(connectionRecommendationPort)
}
