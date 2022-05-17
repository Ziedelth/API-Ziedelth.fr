package fr.ziedelth.controllers

import fr.ziedelth.caches.AnimeCache
import fr.ziedelth.caches.AnimeSimulcastCache
import fr.ziedelth.models.Anime
import fr.ziedelth.utils.Session
import kotlin.math.min

object AnimeController {

    fun searchAnime(country: String, search: String): List<Anime>? {
        val cache = AnimeCache.get(country)
        return cache?.filter { it.name?.contains(search, true) == true }
    }

    fun getAnimesBySimulcast(
        country: String,
        simulcastId: Int,
        page: Int = 1,
        limit: Int = 9
    ): List<Anime>? {
        val cache = AnimeSimulcastCache.get(country, simulcastId)
        return cache?.subList(min(cache.size, (page - 1) * limit), min(cache.size, page * limit))
    }

    fun getAnimeById(id: Long): Anime? {
        val session = Session.sessionFactory.openSession()
        val anime = session?.createQuery(
            "FROM Anime WHERE id = :animeId",
            Anime::class.java
        )?.setParameter("animeId", id)?.list()?.firstOrNull()
        session?.close()
        return anime
    }

    private fun mergeAnime(from: Anime, to: Anime) {
        val session = Session.sessionFactory.openSession()
        val transaction = session.beginTransaction()

        if (to.image.isNullOrEmpty() && !from.image.isNullOrEmpty()) to.image = from.image
        if (to.description.isNullOrEmpty() && !from.description.isNullOrEmpty()) to.description = from.description

        val fromCodes = from.codes
        val toCodes = to.codes

        // Add fromCodes to toCodes if not already present
        toCodes?.addAll(fromCodes?.filter { !toCodes.contains(it) } ?: listOf())

        val fromGenres = from.genres
        val toGenres = to.genres

        // Add fromGenres to toGenres if not already present
        toGenres?.addAll(fromGenres?.filter { !toGenres.contains(it) } ?: listOf())

        // Change from anime id to anime id in episodes
        from.url?.let {
            EpisodeController.getEpisodesByAnime(it)?.forEach { episode ->
                episode.anime = to
                session.persist(episode)
            }
        }

        // Change from anime id to anime id in scans
        from.url?.let {
            ScanController.getScansByAnime(it)?.forEach { scan ->
                scan.anime = to
                session.persist(scan)
            }
        }

        session.persist(to)
        session.remove(from)

        transaction.commit()
        session.close()
    }

    fun mergeAnime(from: Long, to: Long) =
        mergeAnime(getAnimeById(from)!!, getAnimeById(to)!!)

    // Update anime
    fun updateAnime(anime: Anime) {
        val session = Session.sessionFactory.openSession()
        session.beginTransaction()
        session.persist(anime)
        session.flush()
        session.close()
    }
}