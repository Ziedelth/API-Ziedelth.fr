package fr.ziedelth.commands

import fr.ziedelth.caches.EpisodeCache
import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.utils.ICommand
import fr.ziedelth.utils.JFile
import fr.ziedelth.utils.toString
import java.io.File
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicLong
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis

class AnimeCommand : ICommand("anime") {
    override fun run(args: List<String>) {
        if (args.isEmpty()) {
            println("""Usage:
                • anime show
                • anime clean
                • anime optimize
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
                }
            }

            "optimize" -> {
                val folder = JFile("images").file
                val animeFolder = File(folder, "animes")
                val episodeFolder = File(folder, "episodes")

                val animeFiles = animeFolder.listFiles()?.filter { it.extension != "webp" }
                val episodeFiles = episodeFolder.listFiles()?.filter { it.extension != "webp" }

                val totalAnimeOriginalSize = animeFiles?.sumOf { it.length() }
                val totalEpisodeOriginalSize = episodeFiles?.sumOf { it.length() }

                println("Original animes size: ~${totalAnimeOriginalSize?.div((1024 * 1024))} MiB")
                println("Original episodes size: ~${totalEpisodeOriginalSize?.div((1024 * 1024))} MiB")

                val tmpFolder = JFile("tmp").file
                if (!tmpFolder.exists()) tmpFolder.mkdirs()
                val animeTmpFolder = File(tmpFolder, "animes")
                if (!animeTmpFolder.exists()) animeTmpFolder.mkdirs()
                val episodeTmpFolder = File(tmpFolder, "episodes")
                if (!episodeTmpFolder.exists()) episodeTmpFolder.mkdirs()

                val pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
                val callables = mutableListOf<Callable<Any>>()
                val times = mutableListOf<Long>()

                val totalAnimeOptimizedIndex = AtomicLong(0)
                var isDone = false
                val thread1 = Thread {
                    while (!isDone) {
                        val copy = times.toList()
                        println(
                            "Optimizing: ${totalAnimeOptimizedIndex.get()}/${animeFiles?.size} (${
                                animeFiles?.size?.minus(
                                    totalAnimeOptimizedIndex.get()
                                )?.times(copy.average())?.div(1000)?.toInt()
                            } seconds remaining)"
                        )
                        Thread.sleep(1000)
                    }
                }
                println("Optimize animes...")
                animeFiles?.forEach {
                    callables.add(Callable {
                        times.add(measureTimeMillis {
                            ImageIO.write(
                                ImageIO.read(it),
                                "webp",
                                File(animeTmpFolder, "${UUID.randomUUID()}.webp")
                            )
                        })
                        totalAnimeOptimizedIndex.incrementAndGet()
                    })
                }
                thread1.start()
                pool.invokeAll(callables)
                isDone = true
                thread1.join()
                callables.clear()
                times.clear()

                val totalEpisodeOptimizedIndex = AtomicLong(0)
                isDone = false
                val thread2 = Thread {
                    while (!isDone) {
                        val copy = times.toList()
                        println(
                            "Optimizing: ${totalEpisodeOptimizedIndex.get()}/${episodeFiles?.size} (${
                                episodeFiles?.size?.minus(
                                    totalEpisodeOptimizedIndex.get()
                                )?.times(copy.average())?.div(1000)?.toInt()
                            } seconds remaining)"
                        )
                        Thread.sleep(1000)
                    }
                }
                println("Optimize episodes...")
                episodeFiles?.forEach {
                    callables.add(Callable {
                        times.add(measureTimeMillis {
                            ImageIO.write(
                                ImageIO.read(it),
                                "webp",
                                File(episodeTmpFolder, "${UUID.randomUUID()}.webp")
                            )
                        })
                        totalEpisodeOptimizedIndex.incrementAndGet()
                    })
                }
                thread2.start()
                pool.invokeAll(callables)
                isDone = true
                thread2.join()
                callables.clear()
                times.clear()

                val totalAnimeOptimizedSize = animeTmpFolder.listFiles()?.sumOf { it.length() }
                val totalEpisodeOptimizedSize = episodeTmpFolder.listFiles()?.sumOf { it.length() }

                println(
                    "Optimized animes size: ~${totalAnimeOptimizedSize?.div((1024 * 1024))} MiB (~${
                        (totalAnimeOptimizedSize?.toDouble()?.div(totalAnimeOriginalSize!!))?.times(100)?.toString(2)
                    }%)"
                )
                println(
                    "Optimized episodes size: ~${totalEpisodeOptimizedSize?.div((1024 * 1024))} MiB (~${
                        (totalEpisodeOptimizedSize?.toDouble()?.div(totalEpisodeOriginalSize!!))?.times(100)
                            ?.toString(2)
                    }%)"
                )

                tmpFolder.deleteRecursively()
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