package fr.ziedelth

import fr.ziedelth.commands.AnimeCleanCommand
import fr.ziedelth.commands.DeletePlatformCommand
import fr.ziedelth.commands.ShowPlatformsCommand
import fr.ziedelth.routes.*
import fr.ziedelth.utils.Session
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import java.util.*
import kotlin.math.min

fun main() {
    println("Init...")

    println("Init session...")
    Session.init()

    Thread {
        println("Init scanner...")
        val commands = arrayOf(ShowPlatformsCommand(), DeletePlatformCommand(), AnimeCleanCommand())
        val scanner = Scanner(System.`in`)

        while (true) {
            val line = scanner.nextLine()
            val allArgs = line.split(" ")

            val command = allArgs[0]
            val args = allArgs.subList(min(1, allArgs.size), allArgs.size)
            commands.firstOrNull { it.command == command }?.run(args)
        }
    }.start()

    println("Init routes...")
    embeddedServer(Netty, port = 8081, host = "0.0.0.0") {
        install(CORS) {
            println("Init cors...")
            anyHost()
            HttpMethod.DefaultMethods.forEach { allowMethod(it) }
        }

        install(ContentNegotiation) {
            println("Init contentNegotiation...")
            gson {}
        }

        routing {
            println("Init routes...")
            countryRoute()
            platformRoute()
            genreRoute()
            episodeTypeRoute()
            langTypeRoute()
            animeRoute()
            episodeRoute()
            simulcastRoute()
            memberRoute()
            watchlistRoute()
        }

        println("Init done. Listen on port 8081")
    }.start(wait = true)
}
