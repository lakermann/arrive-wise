package ch.lukasakermann.arrivewise.application.service

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.Connection
import ch.lukasakermann.arrivewise.application.domain.ConnectionClass
import ch.lukasakermann.arrivewise.application.domain.Occasion
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class ClassificationService {
    fun findConnection(
        connections: List<Connection>,
        targetArrival: ZonedDateTime,
        occasion: Occasion,
        connectionClass: ConnectionClass,
    ): ClassifiedConnection? =
        connections
            .filter {
                classifyArrivals(it.arrival, targetArrival, getBufferFor(occasion)) == connectionClass
            }.map {
                ClassifiedConnection(
                    connection = it,
                    minutesBeforeTargetArrival = ChronoUnit.MINUTES.between(it.arrival, targetArrival),
                    connectionClass = connectionClass,
                )
            }.maxByOrNull { it.connection.departure }

    fun classifyArrivals(
        arrival: ZonedDateTime,
        targetArrival: ZonedDateTime,
        requiredBuffer: Duration,
    ): ConnectionClass {
        val safeArrival = targetArrival.minus(requiredBuffer.toJavaDuration())

        return when {
            arrival <= safeArrival -> ConnectionClass.SAFE
            arrival <= targetArrival -> ConnectionClass.RISKY
            else -> ConnectionClass.TOO_LATE
        }
    }

    fun getBufferFor(occasion: Occasion): Duration =
        when (occasion) {
            Occasion.CLIENT_MEETING -> 30.minutes
            Occasion.WORKSHOP -> 45.minutes
            Occasion.INTERVIEW -> 40.minutes
            Occasion.INTERNAL_MEETING -> 10.minutes
            Occasion.CASUAL -> 5.minutes
            Occasion.UNKNOWN -> 20.minutes
        }
}
