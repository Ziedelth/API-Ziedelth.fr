package fr.ziedelth.routes

import com.google.gson.Gson
import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.controllers.MemberController
import fr.ziedelth.models.Anime
import fr.ziedelth.utils.Encode
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

                call.respond(animes)
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

                call.respond(animes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        post("/merge") {
            try {
                val formParameters = call.receiveParameters()

                val token = formParameters["token"] ?: return@post call.respond(
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

                val fromId = formParameters["fromId"]?.toLongOrNull() ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "FromId must be an integer"
                )

                val toId = formParameters["toId"]?.toLongOrNull() ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "ToId must be an integer"
                )

                AnimeController.mergeAnime(fromId, toId)
                call.respond(HttpStatusCode.OK, "Merged")
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
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
    }
}