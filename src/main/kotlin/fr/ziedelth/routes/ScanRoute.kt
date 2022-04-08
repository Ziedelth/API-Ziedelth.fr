package fr.ziedelth.routes

import fr.ziedelth.controllers.ScanController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.scanRoute() {
    val scanController = ScanController()

    route("/scans/country/{tag}/page/{page}/limit/{limit}") {
        get {
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

                val scans = scanController.getScans(tag, page, limit) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Scans not found"
                )

                call.respond(scans)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}