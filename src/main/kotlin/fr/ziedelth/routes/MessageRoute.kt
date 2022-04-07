package fr.ziedelth.routes

import fr.ziedelth.models.Message
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Calendar

fun Route.messageRoute() {
    get("/") {
        call.respond(listOf(Message("Hello World", "Ziedelth", Calendar.getInstance())))
    }
}