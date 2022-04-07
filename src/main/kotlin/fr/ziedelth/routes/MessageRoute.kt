package fr.ziedelth.routes

import fr.ziedelth.models.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.Calendar

fun Route.messageRoute() {
    route("/") {
        get {
            call.respond(listOf(Message("Hello World", "Ziedelth", Calendar.getInstance())))
        }

        get("/{text}") {
            val text = call.parameters["text"]

            if (text.isNullOrEmpty()) {
                call.respond(HttpStatusCode.BadRequest, "Text is empty")
                return@get
            }

            call.respond(Message(text, "Ziedelth", Calendar.getInstance()))
        }

        post {
            val message = call.receive<Message>()
            call.respond(message)
        }

        put {
            val message = call.receive<Message>()
            call.respond(message)
        }

        delete {
            val message = call.receive<Message>()
            call.respond(message)
        }
    }
}