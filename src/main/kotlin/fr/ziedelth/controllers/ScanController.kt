package fr.ziedelth.controllers

import fr.ziedelth.caches.ScanAnimeCache
import fr.ziedelth.caches.ScanCache
import fr.ziedelth.models.Scan
import fr.ziedelth.utils.Session
import kotlin.math.min

object ScanController {
    fun getScans(country: String, page: Int = 1, limit: Int = 9): List<Scan>? {
        val cache = ScanCache.get(country)
        return cache?.subList(min(cache.size, (page - 1) * limit), min(cache.size, page * limit))
    }

    fun getScansByAnime(animeUrl: String) = ScanAnimeCache.get(animeUrl)

    // Update scan
    fun updateScan(scan: Scan) {
        val session = Session.sessionFactory.openSession()
        session.beginTransaction()
        session.update(scan)
        session.flush()
        session.close()
    }
}