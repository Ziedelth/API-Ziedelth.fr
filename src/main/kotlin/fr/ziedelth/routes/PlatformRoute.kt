package fr.ziedelth.routes

import fr.ziedelth.controllers.PlatformController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.platformRoute() {
    val platformController = PlatformController()

    route("/platforms") {
        get {
            try {
                val platforms = platformController.getPlatforms() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Platforms not found"
                )
                call.respond(platforms)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}