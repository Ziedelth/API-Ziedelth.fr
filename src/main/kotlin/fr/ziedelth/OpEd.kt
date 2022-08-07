package fr.ziedelth

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.microsoft.playwright.Page
import com.microsoft.playwright.Playwright
import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.utils.Session
import fr.ziedelth.utils.toBrotly
import fr.ziedelth.utils.toGZIP
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.Charset

const val WAIT_BOT_TIME = 60 * 1000L

data class OpEd(val type: String, val url: String, val title: String, val artist: String?)
data class AnimeOpEd(val anime: String, val musics: List<OpEd>)

fun main() {
    Session.init()

    val playwright = Playwright.create()
    val browserType = playwright.chromium()
    val browser = browserType.launch()
    val context = browser.newContext()
    context.setDefaultTimeout(WAIT_BOT_TIME.toDouble())
    context.setDefaultNavigationTimeout(WAIT_BOT_TIME.toDouble())
    val page = context.newPage()
    val animes = AnimeController.getAllWithoutCache()
    val cache = mutableListOf<AnimeOpEd>()
    cache.addAll(Gson().fromJson(File("musics.json").readText(), Array<AnimeOpEd>::class.java).toList())

    animes?.filter { anime -> !cache.any { it.anime == anime.name } }?.forEach { anime ->
        val musics = searchAnime(page, anime.name!!)?.mapNotNull { searchOpEd(page, it) }?.flatten() ?: return@forEach
        cache.add(AnimeOpEd(anime.name, musics))

        val dataMinify = Gson().toJson(cache)
        val dataMinifyToGZIP = dataMinify.toGZIP()
        val dataMinifyToBrotli = dataMinify.toBrotly()
        // Compare length in MiB of brotli, gzip and minify
        val dataMinifyLength = dataMinify.toByteArray().size.toDouble() / 1024
        val dataMinifyToGZIPLength = dataMinifyToGZIP.toByteArray().size.toDouble() / 1024
        val dataMinifyToBrotliLength = dataMinifyToBrotli.toByteArray().size.toDouble() / 1024
        println("${anime.name} - ${String.format("%.2f", dataMinifyLength)} KiB - ${String.format("%.2f", dataMinifyToGZIPLength)} KiB - ${String.format("%.2f", dataMinifyToBrotliLength)} KiB")
        File("musics.json").writeText(dataMinify)
        File("musics.gzip").writeText(dataMinifyToGZIP)
        File("musics.br").writeText(dataMinifyToBrotli)
    }

    browser.close()
    playwright.close()
}

private fun b(page: Page): Boolean {
    val captcha = page.querySelector("#oops-captcha")

    if (captcha != null) {
        println("Bot detected, waiting $WAIT_BOT_TIME ms")
        Thread.sleep(WAIT_BOT_TIME)
        return true
    }

    return false
}

fun searchAnime(page: Page, anime: String): List<String>? {
    val url = "https://www.animesonglyrics.com/results?q=${URLEncoder.encode(anime, Charset.defaultCharset())}"
    println("Searching for $anime ($url)")
    page.navigate(url)

    if (b(page)) return searchAnime(page, anime)
    // Get element with id "titlelist"
    val titleList = page.waitForSelector("#titlelist")?.querySelectorAll(".homesongs")?.map { it.textContent() to it.querySelector("a")?.getAttribute("href") }?.mapIndexed { index, pair -> index to pair }

    println("-".repeat(50))
    println("Anime: $anime")

    titleList?.forEach { (index, pair) ->
        val (name, _) = pair
        println("$index - ${name.replace("\n", "")}")
    }

    var input = readLine()?.trim()

    if (input.isNullOrBlank() || input == "-1") {
        println("Would you like to search for another anime? (y/n)")
        // Check if user write Y/n
        input = readLine()?.trim() ?: return null

        if (input == "y") {
            val search = readLine()?.trim() ?: return null
            return searchAnime(page, search)
        }

        return null
    }

    val split = input.split(",").map { it.trim().toInt() }
    return titleList?.filter { split.contains(it.first) }?.mapNotNull { it.second.second }
}

fun searchOpEd(page: Page, url: String): List<OpEd>? {
    println("Searching for $url")
    page.navigate(url)
    if (b(page)) return searchOpEd(page, url)
    // Get element with id "songlist"
    val songList = page.waitForSelector("#songlist")?.querySelectorAll(".list-group-item")?.filter {
        val attribute = it?.getAttribute("title") ?: return@filter false
        return@filter attribute.contains("Opening", true) || attribute.contains("Ending", true)
    }

    println("-".repeat(50))
    println("Songs: ${songList?.size ?: 0}")

    val elements = songList?.mapNotNull { elementHandle ->
        val attribute = elementHandle?.getAttribute("title") ?: return@mapNotNull null
        val href = elementHandle.getAttribute("href") ?: return@mapNotNull null
        val name = elementHandle.textContent().replace("\n", "")
        println("- $name ($attribute) - $href")
        return@mapNotNull href to attribute
    }

    return elements?.mapNotNull { getYoutubeUrl(page, it) }
}

fun getYoutubeUrl(page: Page, url: Pair<String, String>): OpEd? {
    val type = if (url.second.contains("Opening", true)) "opening" else "ending"
    println("Searching for ${url.second} (${url.first})")
    page.navigate(url.first)
    if (b(page)) return getYoutubeUrl(page, url)
    val text = page.querySelector(".songlinks")?.textContent()?.replace("\n", "") ?: return null
    val title = text.split("Lyrics").firstOrNull()?.trim() ?: return null
    var artist = text.split(" Lyricsby ").lastOrNull()?.trim()
    if (artist?.contains(title, true) == true) artist = null
    var youtubeUrl = searchMusic(title, artist) ?: return null

    if (artist == null) {
        println("Artist not found, url it's correct? $youtubeUrl (y/n)")
        // Check if user write Y/n
        val input = readLine()?.trim() ?: return null

        if (input.equals("n", true)) {
            println("Please enter the correct url :")
            youtubeUrl = readLine()?.trim() ?: return null
        }
    }

    return OpEd(type, youtubeUrl, title, artist)
}

fun searchMusic(title: String, artist: String? = null): String? {
    val conn = URL(
        "https://www.googleapis.com/youtube/v3/search?q=${
            URLEncoder.encode("${artist?.ifBlank { null } ?: ""} $title",
                Charset.defaultCharset())
        }&key=AIzaSyAwBQg6JDixbCDqPU_0B2MGUjnqyq54osw&part=snippet&order=relevance&type=video&playsinline=1&maxResults=5").openConnection()
    conn.addRequestProperty("Referer", "https://www.animesonglyrics.com/")
    val `in` = BufferedReader(InputStreamReader(conn.getInputStream()))
    val content = StringBuilder()
    var inputLine: String?
    while (`in`.readLine().also { inputLine = it } != null) content.append(inputLine)
    `in`.close()
    val jsonObject = Gson().fromJson(content.toString(), JsonObject::class.java)
    val videoId =
        jsonObject.getAsJsonArray("items").firstOrNull { it.isJsonObject }?.asJsonObject?.getAsJsonObject("id")
            ?.get("videoId")?.asString ?: return null
    return "https://www.youtube.com/watch?v=$videoId"
}