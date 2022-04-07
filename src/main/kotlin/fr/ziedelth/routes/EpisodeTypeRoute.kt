package fr.ziedelth.routes

import fr.ziedelth.controllers.EpisodeTypeController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.episodeTypeRoute() {
    val episodeTypeController = EpisodeTypeController()

    route("/episode-types") {
        get {
            try {
                val episodeTypes = episodeTypeController.getEpisodeTypes() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episode types not found"
                )
                call.respond(episodeTypes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}