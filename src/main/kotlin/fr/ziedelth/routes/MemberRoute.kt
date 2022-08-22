package fr.ziedelth.routes

import fr.ziedelth.controllers.MemberController
import fr.ziedelth.controllers.MemberController.withoutSensitiveInformation
import fr.ziedelth.utils.toBrotly
import fr.ziedelth.utils.toJSONString
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.memberRoute() {
    route("/v1/member") {
        route("/{pseudo}") {
            get {
                try {
                    val pseudo = call.parameters["pseudo"] ?: throw IllegalArgumentException("Pseudo is missing")
                    val member =
                        MemberController.getMemberByPseudo(pseudo) ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(member.withoutSensitiveInformation().toJSONString())
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
                    MemberController.register(pseudo, email, password) ?: return@post call.respond(
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
                    val pair = MemberController.loginWithCredentials(email, password)
                    call.respond(pair.first, pair.second.toJSONString())
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
                    val pair = MemberController.loginWithToken(token)
                    call.respond(pair.first, pair.second.toJSONString())
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }
    }

    route("/v2/member") {
        route("/{pseudo}") {
            get {
                try {
                    val pseudo = call.parameters["pseudo"] ?: throw IllegalArgumentException("Pseudo is missing")
                    val member =
                        MemberController.getMemberByPseudo(pseudo) ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(member.withoutSensitiveInformation().toBrotly())
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
                    val pair = MemberController.loginWithCredentials(email, password)
                    call.respond(pair.first, pair.second.toBrotly())
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
                    val pair = MemberController.loginWithToken(token)
                    call.respond(pair.first, pair.second.toBrotly())
                } catch (e: Exception) {
                    e.message?.let { call.respond(HttpStatusCode.InternalServerError, it) }
                }
            }
        }
    }
}