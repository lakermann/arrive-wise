package ch.lukasakermann.arrivewise.application.domain

data class ClassifiedConnection(
    val connection: Connection,
    val connectionClass: ConnectionClass,
    val minutesBeforeTargetArrival: Long,
)

enum class ConnectionClass {
    SAFE,
    RISKY,
    TOO_LATE,
}
