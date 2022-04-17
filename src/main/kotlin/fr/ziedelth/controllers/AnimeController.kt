package fr.ziedelth.controllers

import fr.ziedelth.caches.AnimeSimulcastCache
import fr.ziedelth.models.Anime
import fr.ziedelth.utils.Session
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
        val cache = AnimeSimulcastCache.get(simulcastController, country, simulcastId)
        return cache?.subList(min(cache.size, (page - 1) * limit), min(cache.size, page * limit))
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