package fr.ziedelth.routes

import fr.ziedelth.controllers.SimulcastController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.simulcastRoute() {
    val simulcastController = SimulcastController()

    route("/v1/simulcasts") {
        get {
            val simulcasts = simulcastController.getSimulcasts() ?: return@get call.respond(
                HttpStatusCode.NoContent,
                "Simulcasts not found"
            )

            call.respond(simulcasts)
        }
    }
}