package fr.ziedelth

import fr.ziedelth.routes.*
import fr.ziedelth.utils.Session
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.routing.*

fun main() {
    Session.init()

    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        install(CORS) {
            anyHost()
            HttpMethod.DefaultMethods.forEach { allowMethod(it) }
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
            statisticRoute()
            simulcastRoute()
        }
    }.start(wait = true)
}
