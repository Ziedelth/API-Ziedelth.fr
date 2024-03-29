package fr.ziedelth.routes

import fr.ziedelth.controllers.SimulcastController
import fr.ziedelth.utils.toBrotly
import fr.ziedelth.utils.toJSONString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.simulcastRoute() {
    route("/v1/simulcasts") {
        get {
            val simulcasts = SimulcastController.getSimulcasts() ?: return@get call.respond(
                HttpStatusCode.NoContent,
                "Simulcasts not found"
            )

            call.respond(simulcasts.toJSONString())
        }
    }

    route("/v2/simulcasts") {
        get {
            val simulcasts = SimulcastController.getSimulcasts() ?: return@get call.respond(
                HttpStatusCode.NoContent,
                "Simulcasts not found"
            )

            call.respond(simulcasts.toBrotly())
        }
    }
}