package fr.ziedelth.caches

import fr.ziedelth.utils.ISO8601
import fr.ziedelth.utils.Session
import fr.ziedelth.utils.Simulcast

object SimulcastCache {
    private var cache: Cache<List<Map<String, Any>>?>? = null

    private fun g(): List<Map<String, Any>> {
        val session = Session.sessionFactory.openSession()
        val el =
            session?.createQuery(
                "SELECT DISTINCT releaseDate FROM Episode ORDER BY releaseDate ASC",
                String::class.java
            )
                ?.list()?.toSet()
        val sl =
            session?.createQuery(
                "SELECT DISTINCT releaseDate FROM Scan ORDER BY releaseDate ASC",
                String::class.java
            )
                ?.list()?.toSet()
        session?.close()

        // Create a list with all elements of _el and _sl
        val list = mutableListOf<String>()
        el?.let { list.addAll(it) }
        sl?.let { list.addAll(it) }

        return list.distinct().mapNotNull { Simulcast.getSimulcast(ISO8601.fromUTCDate(it)) }.toSet()
            .mapIndexed { index, simulcast -> mapOf("id" to index + 1, "simulcast" to simulcast) }
    }

    fun get(): List<Map<String, Any>>? {
        val cache = this.cache

        if (cache != null) {
            if (!cache.hasExpired()) return cache.value

            cache.lastCheck = System.currentTimeMillis()
            cache.value = g()
            this.cache = cache
            return cache.value
        }

        this.cache = Cache(System.currentTimeMillis(), g())
        return this.cache?.value
    }
}