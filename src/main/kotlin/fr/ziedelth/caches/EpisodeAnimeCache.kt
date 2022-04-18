package fr.ziedelth.caches

import fr.ziedelth.models.Episode
import fr.ziedelth.utils.Session

object EpisodeAnimeCache {
    private val cache = mutableMapOf<Long, Cache<List<Episode>?>>()

    private fun g(animeId: Long): List<Episode>? {
        val session = Session.jSessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Episode WHERE anime.id = :animeId ORDER BY releaseDate DESC, anime.name, season DESC, number DESC, episodeType.id, langType.id, id DESC",
            Episode::class.java
        )?.setParameter("animeId", animeId)?.list()
        session?.close()
        return list
    }

    fun get(animeId: Long): List<Episode>? {
        val has = this.cache.containsKey(animeId)

        if (has) {
            val cache = this.cache[animeId]!!

            if (!cache.hasExpired()) return cache.value

            cache.lastCheck = System.currentTimeMillis()
            cache.value = g(animeId)
            this.cache[animeId] = cache
            return cache.value
        }

        val cache = Cache(System.currentTimeMillis(), g(animeId))
        this.cache[animeId] = cache
        return cache.value
    }
}