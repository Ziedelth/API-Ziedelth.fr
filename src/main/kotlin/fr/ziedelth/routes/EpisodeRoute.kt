package fr.ziedelth.routes

import com.google.gson.Gson
import fr.ziedelth.controllers.EpisodeController
import fr.ziedelth.controllers.MemberController
import fr.ziedelth.models.Episode
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.episodeRoute() {
    route("/v1/episodes") {
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

                val episodes = EpisodeController.getEpisodes(tag, page, limit) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episodes not found"
                )

                call.respond(episodes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        get("/anime/{url}") {
            try {
                val url = call.parameters["url"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Url not found"
                )

                val episodes = EpisodeController.getEpisodesByAnime(url) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episodes not found"
                )

                call.respond(episodes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        put("/update") {
            try {
                println("A request to update episodes has been received")
                val token = call.request.headers["Authorization"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    "Token not found"
                )

                println("Token: $token")
                val member = MemberController.getMemberByToken(token) ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    "Member not found"
                )

                println("Member role: ${member.role}")
                if (member.role != 100) {
                    return@put call.respond(
                        HttpStatusCode.Forbidden,
                        "You don't have the permission to do that"
                    )
                }

                println("Updating episode")
                val text = call.receiveText()
                println("Body: $text")
                val episode = Gson().fromJson(text, Episode::class.java)
                EpisodeController.updateEpisode(episode)
                call.respond(HttpStatusCode.OK, "Updated")
                println("Episode updated")
            } catch (e: Exception) {
                println("Error: ${e.message}")
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}