package fr.ziedelth.routes

import fr.ziedelth.utils.toBrotly
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.urlRoute() {
    route("/urls") {
        get {
            try {
                call.respond(
                    mapOf(
                        "countries" to "v2/countries",
                        "platforms" to "v2/platforms",
                        "genres" to "v2/genres",
                        "simulcasts" to "v2/simulcasts",
                        "episode-types" to "v2/episode-types",
                        "lang-types" to "v2/lang-types",

                        "episodes" to "v2/episodes/country/%country%/page/%page%/limit/%limit%",
                        "episode-update" to "v1/episodes/update",

                        "animes" to "v2/animes/country/%country%/simulcast/%simulcast%/page/%page%/limit/%limit%",
                        "anime-episodes" to "v2/episodes/anime/%anime%",
                        "anime-search" to "v2/animes/country/%country%/search/%search%",
                        "anime-merge" to "v1/animes/merge",
                        "anime-update" to "v1/animes/update",

                        "watchlist-episodes" to "v2/watchlist/episodes/member/%member%/page/%page%/limit/%limit%",
                        "watchlist-add" to "v1/watchlist/add",
                        "watchlist-remove" to "v1/watchlist/remove",

                        "member" to "v2/member/%member%",
                        "member-register" to "v1/member/register",
                        "member-login" to "v1/member/login",
                        "member-token" to "v1/member/token",
                    ).toBrotly()
                )
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                e.printStackTrace()
            }
        }
    }
}