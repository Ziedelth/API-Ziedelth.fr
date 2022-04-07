package fr.ziedelth.routes

import fr.ziedelth.controllers.GenreController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.genreRoute() {
    val genreController = GenreController()

    route("/genres") {
        get {
            try {
                val genres = genreController.getGenres() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Genres not found"
                )
                call.respond(genres)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}