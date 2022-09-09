package fr.ziedelth.commands

import fr.ziedelth.caches.EpisodeCache
import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.utils.ICommand
import fr.ziedelth.utils.JFile
import java.io.File
import java.util.*
import javax.imageio.ImageIO

class AnimeCommand : ICommand("anime") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println(
                """Usage:
                • anime show
                • anime clean
                • anime merge <from> <to>
            """.trimIndent()
            )
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
                    println(
                        """Usage :
                        • anime clean sql
                        • anime clean images
                    """.trimIndent()
                    )
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
                        val animeImagesDeprecated = animeImagesFiles.filter { image ->
                            animes?.any { anime ->
                                if (anime.image.isNullOrBlank()) false else image.path.replace(
                                    "\\",
                                    "/"
                                ).contains(anime.image!!)
                            } != true
                        }
                        val totalAnimeDeprecatedSize = animeImagesDeprecated.sumOf { it.length() }

                        println("Get all episodes...")
                        val episodes = EpisodeCache.gAll()
                        println("Get all images from episodes folder...")
                        val episodeImagesFiles = File(folder, "episodes").listFiles() ?: emptyArray()
                        println("Calculate deprecated images...")
                        val episodesImagesDeprecated = episodeImagesFiles.filter { image ->
                            episodes?.any { episode ->
                                if (episode.image.isNullOrBlank()) false else image.path.replace(
                                    "\\",
                                    "/"
                                ).contains(episode.image)
                            } != true
                        }
                        val totalEpisodeDeprecatedSize = episodesImagesDeprecated.sumOf { it.length() }

                        println("Animes: ${animeImagesDeprecated.size}/${animeImagesFiles.size} (~${totalAnimeDeprecatedSize / (1024 * 1024)} MiB)")
                        println("Episodes: ${episodesImagesDeprecated.size}/${episodeImagesFiles.size} (~${totalEpisodeDeprecatedSize / (1024 * 1024)} MiB)")

                        println("Delete deprecated images...")
                        animeImagesDeprecated.forEach { it.delete() }
                        episodesImagesDeprecated.forEach { it.delete() }
                        println("Deleted!")
                    }

                    "optimize" -> {
                        val folder = JFile("images").file
                        val animeFolder = File(folder, "animes")
                        val episodeFolder = File(folder, "episodes")

                        val totalAnimeOriginalSize = animeFolder.listFiles()?.sumOf { it.length() }
                        val totalEpisodeOriginalSize = episodeFolder.listFiles()?.sumOf { it.length() }

                        println("Original animes size: ~${totalAnimeOriginalSize?.div((1024 * 1024))} MiB")
                        println("Original episodes size: ~${totalEpisodeOriginalSize?.div((1024 * 1024))} MiB")

                        val tmpFolder = JFile("tmp").file
                        if (!tmpFolder.exists()) tmpFolder.mkdirs()
                        val animeTmpFolder = File(tmpFolder, "animes")
                        if (!animeTmpFolder.exists()) animeTmpFolder.mkdirs()
                        val episodeTmpFolder = File(tmpFolder, "episodes")
                        if (!episodeTmpFolder.exists()) episodeTmpFolder.mkdirs()

                        println("Optimize animes...")
                        animeFolder.listFiles()?.forEach {
                            ImageIO.write(
                                ImageIO.read(it),
                                "webp",
                                File(animeTmpFolder, "${UUID.randomUUID()}.webp")
                            )
                        }
                        println("Optimize episodes...")
                        episodeFolder.listFiles()?.forEach {
                            ImageIO.write(
                                ImageIO.read(it),
                                "webp",
                                File(episodeTmpFolder, "${UUID.randomUUID()}.webp")
                            )
                        }

                        val totalAnimeOptimizedSize = animeTmpFolder.listFiles()?.sumOf { it.length() }
                        val totalEpisodeOptimizedSize = episodeTmpFolder.listFiles()?.sumOf { it.length() }

                        println("Optimized animes size: ~${totalAnimeOptimizedSize?.div((1024 * 1024))} MiB")
                        println("Optimized episodes size: ~${totalEpisodeOptimizedSize?.div((1024 * 1024))} MiB")

                        tmpFolder.deleteRecursively()
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