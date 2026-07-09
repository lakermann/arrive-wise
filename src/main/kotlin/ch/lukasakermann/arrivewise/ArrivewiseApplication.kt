package ch.lukasakermann.arrivewise

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ArrivewiseApplication

fun main(args: Array<String>) {
    runApplication<ArrivewiseApplication>(*args)
}
