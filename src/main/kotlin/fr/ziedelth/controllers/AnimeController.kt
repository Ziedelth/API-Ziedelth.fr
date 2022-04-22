package fr.ziedelth.controllers

import fr.ziedelth.caches.AnimeCache
import fr.ziedelth.caches.AnimeSimulcastCache
import fr.ziedelth.models.Anime
import fr.ziedelth.utils.Session
import kotlin.math.min

class AnimeController {
    fun searchAnime(country: String, search: String): List<Anime>? {
        val cache = AnimeCache.get(country)
        return cache?.filter { it.name?.contains(search, true) == true }
    }

    fun getAnimesByCountry(country: String, page: Int = 1, limit: Int = 9): List<Anime>? {
        val cache = AnimeCache.get(country)
        return cache?.subList(min(cache.size, (page - 1) * limit), min(cache.size, page * limit))
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

    fun getAnime(id: Long): Anime? {
        val session = Session.sessionFactory.openSession()
        val anime = session?.createQuery(
            "FROM Anime WHERE id = :animeId",
            Anime::class.java
        )?.setParameter("animeId", id)?.list()?.firstOrNull()
        session?.close()
        return anime
    }

    fun mergeAnime(from: Anime, to: Anime, episodeController: EpisodeController, scanController: ScanController) {
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
        from.id?.let {
            episodeController.getEpisodesByAnime(it)?.forEach { episode ->
                episode.anime = to
                session.saveOrUpdate(episode)
            }
        }

        // Change from anime id to anime id in scans
        from.id?.let {
            scanController.getScansByAnime(it)?.forEach { scan ->
                scan.anime = to
                session.saveOrUpdate(scan)
            }
        }

        session.saveOrUpdate(to)
        session.delete(from)

        transaction.commit()
        session.close()
    }

    fun mergeAnime(from: Long, to: Long, episodeController: EpisodeController, scanController: ScanController) =
        mergeAnime(getAnime(from)!!, getAnime(to)!!, episodeController, scanController)
}