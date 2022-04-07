package fr.ziedelth.routes

import fr.ziedelth.controllers.AuthorController
import fr.ziedelth.controllers.MessageController
import fr.ziedelth.models.Message
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.messageRoute() {
    val authorController = AuthorController()
    val messageController = MessageController()

    route("/messages") {
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
                val authors = authorController.getAuthors()

                if (authors.isNullOrEmpty()) {
                    return@get call.respond(HttpStatusCode.NoContent, "Authors not found")
                }

                val message =
                    messageController.addMessage(Message(author = authors.firstOrNull(), content = "Hello, World!"))
                call.respond(message)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}