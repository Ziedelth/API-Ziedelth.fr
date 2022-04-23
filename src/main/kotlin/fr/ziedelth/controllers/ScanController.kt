package fr.ziedelth.controllers

import fr.ziedelth.caches.AnimeCache
import fr.ziedelth.caches.ScanAnimeCache
import fr.ziedelth.models.Scan
import fr.ziedelth.utils.Session

class ScanController {
    fun getScans(country: String, page: Int = 1, limit: Int = 9): List<Scan>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.country.tag = :tag ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id, id DESC",
            Scan::class.java
        )?.setParameter("tag", country)?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        list?.forEach { AnimeCache.setUrl(it.anime) }
        session?.close()
        return list
    }

    fun getScansByAnime(animeUrl: String) = ScanAnimeCache.get(animeUrl)
}