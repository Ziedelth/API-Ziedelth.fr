package fr.ziedelth.routes

import fr.ziedelth.controllers.LangTypeController
import fr.ziedelth.utils.toBrotly
import fr.ziedelth.utils.toJSONString
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
                call.respond(langTypes.toJSONString())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }

    route("/v2/lang-types") {
        get {
            try {
                val langTypes = LangTypeController.getLangTypes() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Lang types not found"
                )
                call.respond(langTypes.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}