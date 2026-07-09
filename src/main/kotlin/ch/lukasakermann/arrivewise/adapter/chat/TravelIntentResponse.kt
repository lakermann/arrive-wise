package ch.lukasakermann.arrivewise.adapter.chat

import com.fasterxml.jackson.annotation.JsonPropertyDescription

data class TravelIntentResponse(
    @JsonPropertyDescription("The starting location.")
    val from: String?,
    @JsonPropertyDescription("The destination location.")
    val to: String?,
    @JsonPropertyDescription(
        "The calendar date of the travel. Must be a date only, in ISO-8601 format: yyyy-MM-dd. Null if missing.",
    )
    val date: String?,
    @JsonPropertyDescription(
        "The target arrival time. Must be a time only, in HH:mm format. Null if missing.",
    )
    val targetArrivalTime: String?,
    @JsonPropertyDescription(
        """
        Classify the primary purpose of the travel based on the user's wording.

        CLIENT_MEETING = The user explicitly mentions meeting a client, customer, prospect, or partner.
    
        WORKSHOP = The user explicitly mentions a workshop, training, seminar, design sprint, hackathon, facilitated session, collaborative working session, or similar event.
        Use WORKSHOP whenever the user says "workshop", even if no other business context is provided.
    
        INTERVIEW = The user explicitly mentions an interview, recruiting conversation, hiring discussion, or candidate interview.
    
        INTERNAL_MEETING = The user explicitly mentions a meeting with colleagues, teammates, managers, or other employees of the same company.
    
        CASUAL = The travel is for personal, social, leisure, family, or other non-business purposes.
    
        UNKNOWN = Only use when the travel occasion cannot reasonably be inferred from the user's message.
        Do NOT use UNKNOWN if one of the categories is explicitly mentioned.
        """,
    )
    val occasion: OccasionResponse?,
)

enum class OccasionResponse {
    CLIENT_MEETING,
    WORKSHOP,
    INTERVIEW,
    INTERNAL_MEETING,
    CASUAL,
    UNKNOWN,
}
