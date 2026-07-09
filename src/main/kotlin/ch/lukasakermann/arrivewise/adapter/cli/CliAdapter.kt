package ch.lukasakermann.arrivewise.adapter.cli

import ch.lukasakermann.arrivewise.application.domain.ClassifiedConnection
import ch.lukasakermann.arrivewise.application.domain.TravelIntentException
import ch.lukasakermann.arrivewise.application.port.input.ConnectionRecommendationPort
import org.apache.commons.text.WordUtils
import org.springframework.boot.CommandLineRunner
import java.io.PrintWriter
import java.io.Reader

class CliAdapter(
    private val connectionRecommendationPort: ConnectionRecommendationPort,
    reader: Reader = System.`in`.reader(),
    private val output: PrintWriter = PrintWriter(System.out, true),
) : CommandLineRunner {
    private val input = reader.buffered()

    override fun run(vararg args: String) {
        output.println("""Try: "I'm in Thun and need to be in Bern by 09:00 for a client meeting"""")
        output.println("""Describe your trip, or type "exit" to quit.""")

        while (true) {
            output.print("> ")
            output.flush()

            val question = input.readLine()?.trim() ?: break

            if (question.isBlank()) continue
            if (question.equals("exit", ignoreCase = true)) break

            output.println(processQuestion(question))
        }
    }

    private fun processQuestion(question: String): String {
        try {
            val recommendation =
                connectionRecommendationPort.getConnectionRecommendation(question)

            return buildString {
                appendLine()
                appendLine("✓ Recommended")
                appendLine(formatConnection(recommendation.latestSafeConnection))
                appendLine()
                appendLine("⚠ Latest feasible")
                appendLine(formatConnection(recommendation.latestPossibleConnection))
                appendLine()
                appendLine(
                    WordUtils.wrap(
                        recommendation.recommendation,
                        80,
                        System.lineSeparator(),
                        true,
                    ),
                )
            }
        } catch (exception: TravelIntentException) {
            return "Please provide the following information: ${exception.missingInformation}"
        } catch (exception: Exception) {
            return "Sorry, something went wrong: ${exception.message}"
        }
    }

    private fun formatConnection(classifiedConnection: ClassifiedConnection?): String =
        classifiedConnection?.let {
            buildString {
                appendLine(
                    "  ${it.connection.departure.toLocalTime()} ${it.connection.from} → " +
                        "${it.connection.arrival.toLocalTime()} ${it.connection.to}",
                )
                append("  ${it.minutesBeforeTargetArrival} min buffer")
            }
        } ?: "  No connection found"
}
