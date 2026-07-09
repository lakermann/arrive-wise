package ch.lukasakermann.arrivewise.adapter.connections

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@ActiveProfiles("test")
class ConnectionsSearchAdapterTestIT {
    @Autowired
    private lateinit var connectionsSearchAdapter: ConnectionsSearchAdapter

    @Test
    fun `returns connections between Thun and Bern`() {
        val connections =
            connectionsSearchAdapter.connections(
                from = "Thun",
                to = "Bern",
                date = LocalDate.now().plusDays(1),
                time = LocalTime.of(8, 0),
            )

        assertTrue(connections.isNotEmpty())
        val firstConnection = connections.first()
        assertTrue(firstConnection.from.contains("Thun"))
        assertTrue(firstConnection.to.contains("Bern"))
    }
}
