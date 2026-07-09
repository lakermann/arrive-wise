package ch.lukasakermann.arrivewise.application.port.output

import ch.lukasakermann.arrivewise.application.domain.Connection
import java.time.LocalDate
import java.time.LocalTime

interface ConnectionSearchPort {
    fun connections(
        from: String,
        to: String,
        date: LocalDate,
        time: LocalTime,
    ): List<Connection>
}
