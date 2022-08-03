package fr.ziedelth.routes

import fr.ziedelth.controllers.PlatformController
import fr.ziedelth.utils.toBrotly
import fr.ziedelth.utils.toJSONString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.platformRoute() {
    route("/v1/platforms") {
        get {
            try {
                val platforms = PlatformController.getPlatforms() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Platforms not found"
                )
                call.respond(platforms.toJSONString())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }

    route("/v2/platforms") {
        get {
            try {
                val platforms = PlatformController.getPlatforms() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Platforms not found"
                )
                call.respond(platforms.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}