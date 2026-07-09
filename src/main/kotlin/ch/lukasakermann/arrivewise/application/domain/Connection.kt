package ch.lukasakermann.arrivewise.application.domain

import java.time.ZonedDateTime

data class Connection(
    val from: String,
    val to: String,
    val departure: ZonedDateTime,
    val arrival: ZonedDateTime,
)
