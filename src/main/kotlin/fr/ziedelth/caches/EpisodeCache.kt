package fr.ziedelth.caches

import fr.ziedelth.models.Episode
import fr.ziedelth.utils.Session

object EpisodeCache {
    private var cache = mutableMapOf<String, Cache<List<Episode>?>>()

    fun gAll(): List<Episode>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Episode ORDER BY releaseDate DESC, anime.name, season DESC, number DESC, episodeType.id, langType.id, id DESC",
            Episode::class.java
        )?.list()
        list?.forEach { AnimeCache.setUrl(it.anime) }
        session?.close()
        return list
    }

    private fun g(country: String): List<Episode>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Episode WHERE anime.country.tag = :tag ORDER BY releaseDate DESC, anime.name, season DESC, number DESC, episodeType.id, langType.id, id DESC",
            Episode::class.java
        )?.setParameter("tag", country)?.list()
        list?.forEach { AnimeCache.setUrl(it.anime) }
        session?.close()
        return list
    }

    fun get(country: String): List<Episode>? {
        val has = this.cache.containsKey(country)

        if (has) {
            val cache = this.cache[country]!!

            if (!cache.hasExpired()) return cache.value

            cache.lastCheck = System.currentTimeMillis()
            cache.value = g(country)
            this.cache[country] = cache
            return cache.value
        }

        val cache = Cache(System.currentTimeMillis(), g(country))
        this.cache[country] = cache
        return cache.value
    }
}