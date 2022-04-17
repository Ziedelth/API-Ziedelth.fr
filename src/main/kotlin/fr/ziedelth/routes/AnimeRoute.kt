package fr.ziedelth.routes

import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.controllers.SimulcastController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.animeRoute() {
    val animeController = AnimeController()

    route("/v1/animes") {
        get("/country/{tag}/page/{page}/limit/{limit}") {
            try {
                val tag = call.parameters["tag"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Tag not found"
                )

                val page = call.parameters["page"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Page must be an integer"
                )

                val limit = call.parameters["limit"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Limit must be an integer"
                )

                val animes = animeController.getAnimesByCountry(tag, page, limit) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Animes not found"
                )

                call.respond(animes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        get("/country/{tag}/simulcast/{simulcast}/page/{page}/limit/{limit}") {
            val simulcastController = SimulcastController()

            try {
                val tag = call.parameters["tag"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Tag not found"
                )

                val simulcast = call.parameters["simulcast"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Simulcast must be an integer"
                )

                val page = call.parameters["page"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Page must be an integer"
                )

                val limit = call.parameters["limit"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Limit must be an integer"
                )

                val animes = animeController.getAnimesBySimulcast(tag, simulcastController, simulcast, page, limit)
                    ?: return@get call.respond(
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