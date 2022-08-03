package fr.ziedelth.routes

import com.google.gson.Gson
import com.google.gson.JsonObject
import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.controllers.EpisodeController
import fr.ziedelth.controllers.MemberController
import fr.ziedelth.models.Anime
import fr.ziedelth.utils.toBrotly
import fr.ziedelth.utils.toJSONString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.animeRoute() {
    route("/v1/animes") {
        get("/country/{tag}/simulcast/{simulcast}/page/{page}/limit/{limit}") {
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

                val animes = AnimeController.getAnimesBySimulcast(tag, simulcast, page, limit)
                    ?: return@get call.respond(
                        HttpStatusCode.NoContent,
                        "Animes not found"
                    )

                call.respond(animes.toJSONString())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                e.printStackTrace()
            }
        }

        get("/country/{tag}/search/{search}") {
            try {
                val tag = call.parameters["tag"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Tag not found"
                )

                val search = call.parameters["search"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Search not found"
                )

                val animes = AnimeController.searchAnime(tag, search) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Animes not found"
                )

                call.respond(animes.toJSONString())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        post("/merge") {
            try {
                val token = call.request.header("Authorization") ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Token not found"
                )

                val member = MemberController.getMemberByToken(token) ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Member not found"
                )

                if (member.role != 100) {
                    return@post call.respond(
                        HttpStatusCode.Forbidden,
                        "You don't have the permission to do that"
                    )
                }

                val formParameters = Gson().fromJson(call.receiveText(), JsonObject::class.java)
                val fromId = formParameters.get("fromId").asLong
                val toId = formParameters.get("toId").asLong
                AnimeController.mergeAnime(fromId, toId)
                call.respond(HttpStatusCode.OK, "Merged")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "${e.message} -- ${e.stackTrace}")
                e.printStackTrace()
            }
        }

        put("/update") {
            try {
                val token = call.request.header("Authorization") ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    "Token not found"
                )

                val member = MemberController.getMemberByToken(token) ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    "Member not found"
                )

                if (member.role != 100) {
                    return@put call.respond(
                        HttpStatusCode.Forbidden,
                        "You don't have the permission to do that"
                    )
                }

                val text = call.receiveText()
                val anime = Gson().fromJson(text, Anime::class.java)
                AnimeController.updateAnime(anime)
                call.respond(HttpStatusCode.OK, "Updated")
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                e.printStackTrace()
            }
        }
    }

    route("/v2/animes") {
        get("/country/{tag}/simulcast/{simulcast}/page/{page}/limit/{limit}") {
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

                val animes = AnimeController.getAnimesBySimulcast(tag, simulcast, page, limit)
                    ?: return@get call.respond(
                        HttpStatusCode.NoContent,
                        "Animes not found"
                    )

                call.respond(animes.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                e.printStackTrace()
            }
        }

        get("/country/{tag}/search/{search}") {
            try {
                val tag = call.parameters["tag"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Tag not found"
                )

                val search = call.parameters["search"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Search not found"
                )

                val animes = AnimeController.searchAnime(tag, search) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Animes not found"
                )

                call.respond(animes.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        get("/{url}/episodes/page/{page}/limit/{limit}") {
            try {
                val url = call.parameters["url"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Url not found"
                )

                val page = call.parameters["page"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Page must be an integer"
                )

                val limit = call.parameters["limit"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Limit must be an integer"
                )

                val episodes = EpisodeController.getEpisodesByAnime(url, page, limit) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episodes not found"
                )

                call.respond(episodes.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}