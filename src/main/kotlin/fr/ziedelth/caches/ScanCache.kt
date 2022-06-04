package fr.ziedelth.caches

import fr.ziedelth.models.Scan
import fr.ziedelth.utils.Session

object ScanCache {
    private var cache = mutableMapOf<String, Cache<List<Scan>?>>()

    private fun g(country: String): List<Scan>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.country.tag = :tag ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id, id DESC",
            Scan::class.java
        )?.setParameter("tag", country)?.list()
        list?.forEach { AnimeCache.setUrl(it.anime) }
        session?.close()
        return list
    }

    fun get(country: String): List<Scan>? {
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