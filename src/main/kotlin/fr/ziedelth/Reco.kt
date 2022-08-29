package fr.ziedelth

import fr.ziedelth.controllers.AnimeController
import fr.ziedelth.controllers.MemberController
import fr.ziedelth.utils.Session

fun main() {
    Session.init()

    val watchlistAnime = MemberController.getMemberByPseudo("Ziedelth")?.watchlist ?: return
    val genres = watchlistAnime.mapNotNull { it.genres }.flatten().toSet()
    val animes = AnimeController.getAllWithoutCache() ?: return
    val recommendedAnimes = animes.filter { !watchlistAnime.contains(it) }
        .map { it to (it.genres?.count { genre -> genres.contains(genre) } ?: 0) }.sortedByDescending { it.second }
        .filter { it.second > 0 }

    recommendedAnimes.forEach { pair ->
        println("${pair.first.name} with ${pair.second} genre(s)")
    }
}