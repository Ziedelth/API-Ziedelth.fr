package fr.ziedelth.controllers

import fr.ziedelth.caches.EpisodeAnimeCache
import fr.ziedelth.caches.EpisodeCache
import fr.ziedelth.models.Episode
import fr.ziedelth.utils.Session
import kotlin.math.min

object EpisodeController {
    fun getEpisodes(country: String, page: Int = 1, limit: Int = 9): List<Episode>? {
        val cache = EpisodeCache.get(country)
        return cache?.subList(min(cache.size, (page - 1) * limit), min(cache.size, page * limit))
    }

    fun getEpisodesByAnime(animeUrl: String) = EpisodeAnimeCache.get(animeUrl)
    fun getEpisodesByAnimeWithoutCache(animeId: Long) = EpisodeAnimeCache.g(animeId)

    // Update episode
    fun updateEpisode(episode: Episode) {
        val session = Session.sessionFactory.openSession()
        session.beginTransaction()
        session.update(episode)
        session.flush()
        session.close()
    }
}