package fr.ziedelth.controllers

import fr.ziedelth.caches.EpisodeAnimeCache
import fr.ziedelth.models.Episode
import fr.ziedelth.utils.Session

class EpisodeController {
    fun getEpisodes(country: String, page: Int = 1, limit: Int = 9): List<Episode>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Episode WHERE anime.country.tag = :tag ORDER BY releaseDate DESC, anime.name, season DESC, number DESC, episodeType.id, langType.id, id DESC",
            Episode::class.java
        )?.setParameter("tag", country)?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        session?.close()
        return list
    }

    fun getEpisodesByAnime(animeId: Long) = EpisodeAnimeCache.get(animeId)
}