package fr.ziedelth

import fr.ziedelth.routes.*
import fr.ziedelth.utils.Session
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*

fun main() {
    println("Init...")

    // Print session init
    println("Init session...")
    Session.init()

    println("Init routes...")
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        install(CORS) {
            println("Init cors...")
            anyHost()
            HttpMethod.DefaultMethods.forEach { allowMethod(it) }
        }

        install(ContentNegotiation) {
            println("Init contentNegotiation...")
            gson {}
        }

        routing {
            println("Init routes...")
            countryRoute()
            platformRoute()
            genreRoute()
            episodeTypeRoute()
            langTypeRoute()
            animeRoute()
            episodeRoute()
            scanRoute()
            simulcastRoute()
            memberRoute()
            watchlistRoute()
        }

        println("Init done. Listen on port 8081")
    }.start(wait = true)
}
