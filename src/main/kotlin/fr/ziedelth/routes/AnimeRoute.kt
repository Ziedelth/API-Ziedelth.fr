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
            try {
                val animes = animeController.getAnimes() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Animes not found"
                )

                call.respond(animes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        get("/{tag}") {
            try {
                val tag = call.parameters["tag"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Tag not found"
                )

                val animes = animeController.getAnimesByCountry(tag) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Animes not found"
                )

                call.respond(animes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}