package fr.ziedelth.controllers

import fr.ziedelth.models.Scan
import fr.ziedelth.utils.Session

class ScanController {
    fun getScans(country: String, page: Int = 1, limit: Int = 9): List<Scan>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.country.tag = :tag ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id, id DESC",
            Scan::class.java
        )?.setParameter("tag", country)?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        session?.close()
        return list
    }

    fun getScansByAnime(animeId: Long): List<Scan>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.id = :animeId ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id, id DESC",
            Scan::class.java
        )?.setParameter("animeId", animeId)?.list()
        session?.close()
        return list
    }
}