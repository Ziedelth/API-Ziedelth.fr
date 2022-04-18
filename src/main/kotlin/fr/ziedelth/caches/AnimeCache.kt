package fr.ziedelth.caches

import fr.ziedelth.models.Anime
import fr.ziedelth.utils.Session

object AnimeCache {
    private val cache = mutableMapOf<String, Cache<List<Anime>?>>()

    private fun g(country: String): List<Anime>? {
        val session = Session.jSessionFactory.openSession()
        val list = session?.createQuery("FROM Anime WHERE country.tag = :tag ORDER BY name", Anime::class.java)
            ?.setParameter("tag", country)?.list()
        session?.close()
        return list
    }

    fun get(country: String): List<Anime>? {
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