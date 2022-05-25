package fr.ziedelth.routes

import fr.ziedelth.controllers.GenreController
import fr.ziedelth.utils.toBrotly
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.genreRoute() {
    route("/v1/genres") {
        get {
            try {
                val genres = GenreController.getGenres() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Genres not found"
                )
                call.respond(genres)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }

    route("/v2/genres") {
        get {
            try {
                val genres = GenreController.getGenres() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Genres not found"
                )
                call.respond(genres.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}