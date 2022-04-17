package fr.ziedelth.controllers

import fr.ziedelth.models.Anime
import fr.ziedelth.models.Episode
import fr.ziedelth.utils.ISO8601
import fr.ziedelth.utils.Session
import fr.ziedelth.utils.Simulcast
import kotlin.math.min

class AnimeController {
    fun getAnimesByCountry(country: String, page: Int = 1, limit: Int = 9): List<Anime>? {
        val session = Session.sessionFactory.openSession()
        val list =
            session?.createQuery("FROM Anime WHERE country.tag = :tag ORDER BY name", Anime::class.java)
                ?.setParameter("tag", country)
                ?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        session?.close()
        return list
    }

    fun getAnimesBySimulcast(
        country: String,
        simulcastController: SimulcastController,
        simulcastId: Int,
        page: Int = 1,
        limit: Int = 9
    ): List<Anime>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM Episode WHERE anime.country.tag = :tag ORDER BY anime.name", Episode::class.java)
            ?.setParameter("tag", country)?.list()
        session?.close()

        val simulcasts = simulcastController.getSimulcasts() ?: return null
        val getSimulcast = simulcasts.firstOrNull { it["id"] == simulcastId } ?: return null
        val filter = list?.filter { Simulcast.getSimulcast(ISO8601.fromUTCDate(it.releaseDate)) == getSimulcast["simulcast"] }?.mapNotNull { it.anime }?.toSet()?.toList() ?: return null
        return filter.subList(min(filter.size, (page - 1) * limit), min(filter.size, page * limit))
    }

    fun mergeAnime(from: Anime, to: Anime) {
        // Get from anime id
        // Get to anime id
        // Get codes from anime
        // Add codes to anime
        // Get genres from anime
        // Change from anime id to anime id in episodes
        // Change from anime id to anime id in scans
    }
}