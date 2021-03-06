package fr.ziedelth.caches

import fr.ziedelth.models.Anime
import fr.ziedelth.utils.Session

object AnimeCache {
    private val cache = mutableMapOf<String, Cache<List<Anime>?>>()

    fun setUrl(anime: Anime?) {
        anime?.url =
            anime?.name?.lowercase()?.filter { c -> c.isLetterOrDigit() || c.isWhitespace() || c == '-' }?.trim()
                ?.replace("\\s+".toRegex(), "-")?.replace("--", "-")
    }

    fun setUrl(list: Iterable<Anime>?) {
        list?.forEach {
            setUrl(it)
        }
    }

    fun gAll(): List<Anime>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM Anime ORDER BY name", Anime::class.java)
            ?.list()
        setUrl(list)
        session?.close()
        return list
    }

    private fun g(country: String): List<Anime>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM Anime WHERE country.tag = :tag ORDER BY name", Anime::class.java)
            ?.setParameter("tag", country)?.list()
        setUrl(list)
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