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
        route("/{pseudo}") {
            get {
                try {
                    val pseudo = call.parameters["pseudo"] ?: throw IllegalArgumentException("Pseudo is missing")
                    val member = memberController.getMemberByPseudo(pseudo) ?: return@get call.respond(HttpStatusCode.NotFound)
                    // Create a new member without password
                    val memberWithoutPassword = member.copy(email = null, emailVerified = null, password = null, lastLogin = null, token = null)
                    call.respond(memberWithoutPassword)
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }

        route("/register") {
            post {
                try {
                    val formParameters = call.receiveParameters()
                    // Get received pseudo
                    val pseudo = formParameters["pseudo"] ?: throw IllegalArgumentException("Pseudo is missing")
                    // Get received email
                    val email = formParameters["email"] ?: throw IllegalArgumentException("Email is missing")
                    // Get received password
                    val password = formParameters["password"] ?: throw IllegalArgumentException("Password is missing")

                    // Register member
                    memberController.register(pseudo, email, password) ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Member already exists"
                    )

                    // Return member
                    call.respond(HttpStatusCode.Created, "OK")
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }

        route("/login") {
            post {
                try {
                    val formParameters = call.receiveParameters()
                    // Get received email
                    val email = formParameters["email"] ?: throw IllegalArgumentException("Email is missing")
                    // Get received password
                    val password = formParameters["password"] ?: throw IllegalArgumentException("Password is missing")

                    // Login member
                    val pair = memberController.loginWithCredentials(email, password)
                    call.respond(pair.first, pair.second)
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }

        route("/token") {
            post {
                try {
                    val formParameters = call.receiveParameters()
                    // Get received token
                    val token = formParameters["token"] ?: throw IllegalArgumentException("Token is missing")

                    // Login member
                    val pair = memberController.loginWithToken(token)
                    call.respond(pair.first, pair.second)
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }
    }
}