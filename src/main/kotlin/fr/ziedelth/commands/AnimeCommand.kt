package fr.ziedelth.commands

import fr.ziedelth.caches.EpisodeCache
import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.utils.ICommand
import fr.ziedelth.utils.JFile
import java.io.File

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
                if (args.size == 1) {
                    println("""Usage :
                        • anime clean sql
                        • anime clean images
                    """.trimIndent())
                    return
                }

                when (args[1]) {
                    "sql" -> {
                        println("Cleaning animes...")
                        AnimeController.clean()
                        println("Cleaned!")
                    }
                    "images" -> {
                        val folder = JFile("images").file

                        println("Get all animes...")
                        val animes = AnimeController.getAllWithoutCache()
                        println("Get all images from animes folder...")
                        val animeImagesFiles = File(folder, "animes").listFiles() ?: emptyArray()
                        println("Calculate deprecated images...")
                        val animeImagesDeprecated = animeImagesFiles.filter { image -> animes?.any { anime -> if (anime.image.isNullOrBlank()) false else image.path.replace("\\", "/").contains(anime.image!!) } != true }
                        val totalAnimeDeprecatedSize = animeImagesDeprecated.sumOf { it.length() }

                        println("Get all episodes...")
                        val episodes = EpisodeCache.gAll()
                        println("Get all images from episodes folder...")
                        val episodeImagesFiles = File(folder, "episodes").listFiles() ?: emptyArray()
                        println("Calculate deprecated images...")
                        val episodesImagesDeprecated = episodeImagesFiles.filter { image -> episodes?.any { episode -> if (episode.image.isNullOrBlank()) false else image.path.replace("\\", "/").contains(episode.image) } != true }
                        val totalEpisodeDeprecatedSize = episodesImagesDeprecated.sumOf { it.length() }

                        println("Animes: ${animeImagesDeprecated.size}/${animeImagesFiles.size} (~${totalAnimeDeprecatedSize / (1024 * 1024)} MiB)")
                        println("Episodes: ${episodesImagesDeprecated.size}/${episodeImagesFiles.size} (~${totalEpisodeDeprecatedSize / (1024)} KiB)")
                    }
                }
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