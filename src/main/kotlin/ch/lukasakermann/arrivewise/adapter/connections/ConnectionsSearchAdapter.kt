package ch.lukasakermann.arrivewise.adapter.connections

import ch.lukasakermann.arrivewise.application.domain.Connection
import ch.lukasakermann.arrivewise.application.port.output.ConnectionSearchPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.time.LocalDate
import java.time.LocalTime

@Component
class ConnectionsSearchAdapter(
    private val connectionMapper: ConnectionMapper,
) : ConnectionSearchPort {
    private val log = LoggerFactory.getLogger(ConnectionsSearchAdapter::class.java)

    private val client =
        RestClient
            .builder()
            .baseUrl("https://transport.opendata.ch/v1")
            .build()

    override fun connections(
        from: String,
        to: String,
        date: LocalDate,
        time: LocalTime,
    ): List<Connection> {
        log.info("Searching public transport connections from {} to {}", from, to)

        val wrapper =
            client
                .get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/connections")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .queryParam("date", date)
                        .queryParam("time", time)
                        .queryParam("isArrivalTime", 1)
                        .queryParam("limit", 16)
                        .build()
                }.retrieve()
                .body<ConnectionsResponse>()

        log.info("Transport response: {}", wrapper)

        return wrapper?.let(connectionMapper::toConnections).orEmpty()
    }
}
