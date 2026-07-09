package ch.lukasakermann.arrivewise.application.domain

class TravelIntentException(
    message: String,
    val missingInformation: String,
) : RuntimeException(message)
