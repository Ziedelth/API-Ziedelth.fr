package fr.ziedelth.routes

import fr.ziedelth.controllers.StatisticController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.statisticRoute() {
    val statisticController = StatisticController()

    route("/v1/statistics") {
        get("/count/{days}") {
            try {
                val days = call.parameters["days"]?.toIntOrNull() ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Days must be an integer"
                )

                val count = statisticController.getCount(days)
                call.respond(count)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}