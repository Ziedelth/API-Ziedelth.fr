package fr.ziedelth.routes

import fr.ziedelth.controllers.AuthorController
import fr.ziedelth.models.Author
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.messageAuthor() {
    val authorController = AuthorController()

    route("/authors") {
        get {
            try {
                val authors = authorController.getAuthors() ?: return@get call.respond(
                    HttpStatusCode.NoContent,
                    "Authors not found"
                )
                call.respond(authors)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }

        get("/add") {
            try {
                val author = authorController.addAuthor(Author(name = "Ziedelth"))
                call.respond(author)
            } catch (e: Exception) {
                e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
            }
        }
    }
}