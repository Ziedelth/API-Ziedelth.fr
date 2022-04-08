package fr.ziedelth.routes

import fr.ziedelth.controllers.EpisodeController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.episodeRoute() {
    val episodeController = EpisodeController()

    route("/episodes") {
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

                val episodes = episodeController.getEpisodes(tag, page, limit) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episodes not found"
                )

                call.respond(episodes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        get("/anime/{id}") {
            try {
                val id = call.parameters["id"]?.toLongOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Id must be an integer"
                )

                val episodes = episodeController.getEpisodesByAnime(id) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episodes not found"
                )

                call.respond(episodes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}