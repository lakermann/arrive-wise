package ch.lukasakermann.arrivewise.adapter.connections

import ch.lukasakermann.arrivewise.application.domain.Connection
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class ConnectionMapper(
    private val clock: Clock,
) {
    fun toConnections(response: ConnectionsResponse): List<Connection> =
        response.connections.map {
            Connection(
                it.from.station.name,
                it.to.station.name,
                it.from.departure!!.atZoneSameInstant(clock.zone),
                it.to.arrival!!.atZoneSameInstant(clock.zone),
            )
        }
}
