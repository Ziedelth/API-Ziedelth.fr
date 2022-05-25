package fr.ziedelth.routes

import fr.ziedelth.controllers.CountryController
import fr.ziedelth.utils.toBrotly
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.countryRoute() {
    route("/v1/countries") {
        get {
            try {
                val countries = CountryController.getCountries() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Countries not found"
                )
                call.respond(countries)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }

    route("/v2/countries") {
        get {
            try {
                val countries = CountryController.getCountries() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Countries not found"
                )
                call.respond(countries.toBrotly())
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}