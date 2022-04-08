package fr.ziedelth.routes

import fr.ziedelth.controllers.AnimeController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.animeRoute() {
    val animeController = AnimeController()

    route("/animes") {
        get {
            println("GET /animes")

            try {
                val animes = animeController.getAnimes()

                if (animes == null) {
                    println("Animes is null")

                    return@get call.respond(
                        HttpStatusCode.NoContent,
                        "Animes not found"
                    )
                }

                call.respond(animes)
            } catch (e: Exception) {
                println(e)
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

    }
}