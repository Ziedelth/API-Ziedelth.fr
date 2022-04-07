package fr.ziedelth

import fr.ziedelth.routes.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            gson {
            }
        }

        routing {
            countryRoute()
            platformRoute()
            genreRoute()
            episodeTypeRoute()
            langTypeRoute()

            messageAuthor()
            messageRoute()
        }
    }.start(wait = true)
}
