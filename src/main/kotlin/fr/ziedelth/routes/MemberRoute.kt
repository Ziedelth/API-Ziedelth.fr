package fr.ziedelth.routes

import fr.ziedelth.controllers.MemberController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.memberRoute() {
    val memberController = MemberController()

    route("/v1/member") {
        route("/register") {
            post {
                val formParameters = call.receiveParameters()
                // Get received pseudo
                val pseudo = formParameters["pseudo"] ?: throw IllegalArgumentException("Pseudo is missing")
                // Get received email
                val email = formParameters["email"] ?: throw IllegalArgumentException("Email is missing")
                // Get received password
                val password = formParameters["password"] ?: throw IllegalArgumentException("Password is missing")

                // Register member
                val member = memberController.register(pseudo, email, password) ?: return@post call.respond(
                    HttpStatusCode.BadRequest,
                    "Member already exists"
                )

                // Return member
                call.respond(HttpStatusCode.Created, member)
            }
        }

        route("/login") {
            post {
                val formParameters = call.receiveParameters()
                // Get received email
                val email = formParameters["email"] ?: throw IllegalArgumentException("Email is missing")
                // Get received password
                val password = formParameters["password"] ?: throw IllegalArgumentException("Password is missing")

                // Login member
                val pair = memberController.loginWithCredentials(email, password)
                call.respond(pair.first, pair.second)
            }
        }
    }
}