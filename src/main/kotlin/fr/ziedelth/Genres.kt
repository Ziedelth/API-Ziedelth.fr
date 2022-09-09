package fr.ziedelth

import com.google.gson.Gson
import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.controllers.GenreController
import fr.ziedelth.utils.GenreEnum
import fr.ziedelth.utils.Session
import fr.ziedelth.utils.toBrotly
import fr.ziedelth.utils.toGZIP
import java.io.File
import java.net.URLEncoder
import java.nio.charset.Charset


data class AnimeGenre(val anime: String, val genres: List<String>)

fun main() {
    Session.init()

    val animes = AnimeController.getAllWithoutCache()
    val cache = mutableListOf<AnimeGenre>()

    val file = File("genres.json")

    if (file.exists())
        cache.addAll(Gson().fromJson(file.readText(), Array<AnimeGenre>::class.java).toList())

    val genres = GenreController.getGenres()
    val check = animes?.filter { anime -> !cache.any { it.anime == anime.name } }
    println("${check?.size} animes to check")

    check?.forEachIndexed { index, anime ->
        if (anime.name.isNullOrBlank()) return@forEachIndexed
        val url = "https://www.nautiljon.com/animes/?q=${URLEncoder.encode(anime.name, Charset.defaultCharset())}"
        println("${anime.name} : $url")
        val stringBuilder = StringBuilder()
        var line: String? = null

        while (line != "ok") {
            line = readLine()
            if (line != "ok")
                stringBuilder.append(line?.split(":")?.last()).append(" - ")
        }

        println(stringBuilder)
        val ge = stringBuilder.split(" - ").map { g -> GenreEnum.getGenre(g.trim()).name }.toMutableList()
        ge.removeIf { it == "UNKNOWN" }
        println("Genres: ${ge.joinToString(", ")}")
        cache.add(AnimeGenre(anime.name, ge))

        val dataMinify = Gson().toJson(cache)
        val dataMinifyToGZIP = dataMinify.toGZIP()
        val dataMinifyToBrotli = dataMinify.toBrotly()
        // Compare length in MiB of brotli, gzip and minify
        val dataMinifyLength = dataMinify.toByteArray().size.toDouble() / 1024
        val dataMinifyToGZIPLength = dataMinifyToGZIP.toByteArray().size.toDouble() / 1024
        val dataMinifyToBrotliLength = dataMinifyToBrotli.toByteArray().size.toDouble() / 1024
        println(
            "${anime.name} - ${String.format("%.2f", dataMinifyLength)} KiB - ${
                String.format(
                    "%.2f",
                    dataMinifyToGZIPLength
                )
            } KiB - ${String.format("%.2f", dataMinifyToBrotliLength)} KiB"
        )
        file.writeText(dataMinify)
        File("genres.gzip").writeText(dataMinifyToGZIP)
        File("genres.br").writeText(dataMinifyToBrotli)

        println("${index + 1}/${check.size}")
    }
}