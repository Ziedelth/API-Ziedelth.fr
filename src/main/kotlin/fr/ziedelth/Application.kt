package fr.ziedelth

import fr.ziedelth.routes.*
import fr.ziedelth.utils.Session
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*

fun main() {
    Session.init()

    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        install(CORS) {
            allowCredentials = true
            maxAgeInSeconds = 3600
            anyHost()
            HttpMethod.DefaultMethods.forEach { method(it) }
        }

        install(ContentNegotiation) {
            gson {}
        }

        routing {
            countryRoute()
            platformRoute()
            genreRoute()
            episodeTypeRoute()
            langTypeRoute()
            animeRoute()
            episodeRoute()
            scanRoute()
        }
    }.start(wait = true)
}
