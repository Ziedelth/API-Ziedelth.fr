package fr.ziedelth.routes

import fr.ziedelth.controllers.EpisodeTypeController
import fr.ziedelth.utils.toBrotly
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.episodeTypeRoute() {
    route("/v1/episode-types") {
        get {
            try {
                val episodeTypes = EpisodeTypeController.getEpisodeTypes() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episode types not found"
                )
                call.respond(episodeTypes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }

    route("/v2/episode-types") {
        get {
            try {
                val episodeTypes = EpisodeTypeController.getEpisodeTypes() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episode types not found"
                )
                call.respond(episodeTypes.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}