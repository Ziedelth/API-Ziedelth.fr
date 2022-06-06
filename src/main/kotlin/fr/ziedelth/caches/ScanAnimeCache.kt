package fr.ziedelth.caches

import fr.ziedelth.models.Anime
import fr.ziedelth.models.Scan
import fr.ziedelth.utils.Session

object ScanAnimeCache {
    private var animesCache: Cache<List<Anime>?>? = null
    private val cache = mutableMapOf<String, Cache<List<Scan>?>>()

    fun g(animeId: Long): List<Scan>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.id = :animeId ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id, id DESC",
            Scan::class.java
        )?.setParameter("animeId", animeId)?.list()
        list?.forEach { AnimeCache.setUrl(it.anime) }
        session?.close()
        return list
    }

    private fun ag(): List<Anime>? {
        val has = this.animesCache != null

        if (has) {
            if (!this.animesCache!!.hasExpired()) return this.animesCache!!.value

            this.animesCache!!.lastCheck = System.currentTimeMillis()
            this.animesCache!!.value = AnimeCache.gAll()
            return this.animesCache!!.value
        }

        val cache = Cache(System.currentTimeMillis(), AnimeCache.gAll())
        this.animesCache = cache
        return cache.value
    }

    fun get(animeUrl: String): List<Scan>? {
        val animeId = ag()?.firstOrNull { it.url == animeUrl }?.id ?: return null
        val has = this.cache.containsKey(animeUrl)

        if (has) {
            val cache = this.cache[animeUrl]!!

            if (!cache.hasExpired()) return cache.value

            cache.lastCheck = System.currentTimeMillis()
            cache.value = g(animeId)
            this.cache[animeUrl] = cache
            return cache.value
        }

        val cache = Cache(System.currentTimeMillis(), g(animeId))
        this.cache[animeUrl] = cache
        return cache.value
    }
}