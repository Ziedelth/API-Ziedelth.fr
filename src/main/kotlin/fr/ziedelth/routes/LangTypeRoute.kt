package fr.ziedelth.routes

import fr.ziedelth.controllers.LangTypeController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.langTypeRoute() {
    route("/v1/lang-types") {
        get {
            try {
                val langTypes = LangTypeController.getLangTypes() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Lang types not found"
                )
                call.respond(langTypes)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}