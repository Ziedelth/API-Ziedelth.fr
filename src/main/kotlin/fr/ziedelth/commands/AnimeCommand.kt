package fr.ziedelth.commands

import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.utils.ICommand

class AnimeCommand : ICommand("anime") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println("""Usage:
                • anime show
                • anime clean
                • anime merge <from> <to>
            """.trimIndent())
            return
        }

        when (args.first()) {
            "show" -> {
                AnimeController.getAllWithoutCache()?.forEach {
                    println("• (${it.id}) ${it.name}")
                }
            }
            "clean" -> {
                println("Cleaning animes...")
                AnimeController.clean()
                println("Cleaned!")
            }
            "merge" -> {
                if (args.size != 3) {
                    println("Usage: anime merge <from> <to>")
                    return
                }

                val from = args[1].toLongOrNull() ?: return
                val to = args[2].toLongOrNull() ?: return
                AnimeController.mergeAnime(from, to)
                println("Ok!")
            }
        }
    }
}