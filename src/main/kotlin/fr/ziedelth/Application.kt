package fr.ziedelth

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

data class Message(val message: String, val author: String)

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(ContentNegotiation) {
            gson {
            }
        }

        routing {
            get("/") {
                call.respondText("Hello World!")
            }

            get("/json") {
                call.respond(listOf(Message("Hello World!", "Ktor")))
            }
        }
    }.start(wait = true)
}
