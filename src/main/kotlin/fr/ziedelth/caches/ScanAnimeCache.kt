package fr.ziedelth.caches

import fr.ziedelth.models.Scan
import fr.ziedelth.utils.Session

object ScanAnimeCache {
    private val cache = mutableMapOf<Long, Cache<List<Scan>?>>()

    private fun g(animeId: Long): List<Scan>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.id = :animeId ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id, id DESC",
            Scan::class.java
        )?.setParameter("animeId", animeId)?.list()
        session?.close()
        return list
    }

    fun get(animeId: Long): List<Scan>? {
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