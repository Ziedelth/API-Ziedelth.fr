package fr.ziedelth.routes

import com.google.gson.Gson
import fr.ziedelth.controllers.MemberController
import fr.ziedelth.controllers.ScanController
import fr.ziedelth.models.Scan
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.scanRoute() {
    route("/v1/scans") {
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

                val scans = ScanController.getScans(tag, page, limit) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Scans not found"
                )

                call.respond(scans)
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

                val scans = ScanController.getScansByAnime(url) ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Episodes not found"
                )

                call.respond(scans)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        put("/update") {
            try {
                val token = call.request.headers["Authorization"] ?: return@put call.respond(
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
                val scan = Gson().fromJson(text, Scan::class.java)
                ScanController.updateScan(scan)
                call.respond(HttpStatusCode.OK, "Updated")
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}