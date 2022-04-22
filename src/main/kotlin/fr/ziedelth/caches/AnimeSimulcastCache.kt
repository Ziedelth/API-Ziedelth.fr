package fr.ziedelth.caches

import fr.ziedelth.controllers.SimulcastController
import fr.ziedelth.models.Anime
import fr.ziedelth.models.Episode
import fr.ziedelth.models.Scan
import fr.ziedelth.utils.ISO8601
import fr.ziedelth.utils.Session
import fr.ziedelth.utils.Simulcast

object AnimeSimulcastCache {
    private val cache = mutableMapOf<Pair<String, Int>, Cache<List<Anime>?>>()

    private fun b(
        it: Map.Entry<Pair<String, Int>, Cache<List<Anime>?>>,
        country: String,
        simulcastId: Int
    ) = it.key.first == country && it.key.second == simulcastId

    private fun g(simulcastController: SimulcastController, country: String, simulcastId: Int): List<Anime>? {
        val session = Session.sessionFactory.openSession()
        val _el =
            session?.createQuery("FROM Episode WHERE anime.country.tag = :tag ORDER BY anime.name", Episode::class.java)
                ?.setParameter("tag", country)?.list()
        val _sl =
            session?.createQuery("FROM Scan WHERE anime.country.tag = :tag ORDER BY anime.name", Scan::class.java)
                ?.setParameter("tag", country)?.list()
        session?.close()

        val simulcasts = simulcastController.getSimulcasts() ?: return null
        val getSimulcast = simulcasts.firstOrNull { it["id"] == simulcastId } ?: return null

        val _ael =
            _el?.filter { Simulcast.getSimulcast(ISO8601.fromUTCDate(it.releaseDate)) == getSimulcast["simulcast"] }
                ?.mapNotNull { it.anime }?.toSet()?.toList() ?: arrayListOf()
        val _asl =
            _sl?.filter { Simulcast.getSimulcast(ISO8601.fromUTCDate(it.releaseDate)) == getSimulcast["simulcast"] }
                ?.mapNotNull { it.anime }?.toSet()?.toList() ?: arrayListOf()

        return _ael.union(_asl).distinctBy { it.id }.sortedBy { it.name?.lowercase() }
    }

    fun get(simulcastController: SimulcastController, country: String, simulcastId: Int): List<Anime>? {
        // If the cache in key contains the country and the simulcast id
        val has = this.cache.any { b(it, country, simulcastId) }

        if (has) {
            val filter = this.cache.filter { b(it, country, simulcastId) }

            val key = filter.keys.first()
            val cache = filter.values.first()

            if (!cache.hasExpired()) return cache.value

            cache.lastCheck = System.currentTimeMillis()
            cache.value = g(simulcastController, country, simulcastId)
            // Replace the cache
            this.cache.replace(key, cache)
            return cache.value
        }

        val key = Pair(country, simulcastId)
        val cache = Cache(System.currentTimeMillis(), g(simulcastController, country, simulcastId))
        this.cache[key] = cache
        return cache.value
    }
}