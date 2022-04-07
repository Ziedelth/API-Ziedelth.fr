package fr.ziedelth.routes

import fr.ziedelth.controllers.MessageController
import fr.ziedelth.models.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.messageRoute() {
    val messageController = MessageController()

    route("/") {
        get {
            try {
                val messages = messageController.getMessages() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Messages not found"
                )
                call.respond(messages)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        get("/add") {
            try {
                val message = messageController.addMessage(Message(null, "Hello world", "Ziedelth", Calendar.getInstance()))
                call.respond(message)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}