package fr.ziedelth.routes

import fr.ziedelth.controllers.WatchlistController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.watchlistRoute() {
    val watchlistController = WatchlistController()

    route("/v1/watchlist") {
        route("/episodes/member/{pseudo}/page/{page}/limit/{limit}") {
            get {
                try {
                    val pseudo = call.parameters["pseudo"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Pseudo not found"
                    )

                    val page = call.parameters["page"]?.toIntOrNull() ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Page must be an integer"
                    )

                    val limit = call.parameters["limit"]?.toIntOrNull() ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Limit must be an integer"
                    )

                    val watchlist = watchlistController.getWatchlistEpisodes(pseudo, page, limit) ?: return@get call.respond(
                        HttpStatusCode.NoContent,
                        "Episodes not found"
                    )

                    call.respond(watchlist)
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }

        route("/scans/member/{pseudo}/page/{page}/limit/{limit}") {
            get {
                try {
                    val pseudo = call.parameters["pseudo"] ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Pseudo not found"
                    )

                    val page = call.parameters["page"]?.toIntOrNull() ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Page must be an integer"
                    )

                    val limit = call.parameters["limit"]?.toIntOrNull() ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Limit must be an integer"
                    )

                    val watchlist = watchlistController.getWatchlistScans(pseudo, page, limit) ?: return@get call.respond(
                        HttpStatusCode.NoContent,
                        "Scans not found"
                    )

                    call.respond(watchlist)
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }

        route("/add") {
            post {
                try {
                    val formParameters = call.receiveParameters()
                    // Get received token
                    val token = formParameters["token"] ?: throw IllegalArgumentException("Token is missing")
                    // Get received anime id
                    val animeId = formParameters["animeId"]?.toLongOrNull() ?: throw IllegalArgumentException("Anime id is missing")
                    call.respond(watchlistController.addToWatchlist(token, animeId))
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }

        route("/remove") {
            post {
                try {
                    val formParameters = call.receiveParameters()
                    // Get received token
                    val token = formParameters["token"] ?: throw IllegalArgumentException("Token is missing")
                    // Get received anime id
                    val animeId = formParameters["animeId"]?.toLongOrNull() ?: throw IllegalArgumentException("Anime id is missing")
                    call.respond(watchlistController.removeToWatchlist(token, animeId))
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }
    }
}