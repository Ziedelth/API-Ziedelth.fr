package fr.ziedelth.controllers

import fr.ziedelth.models.Anime
import fr.ziedelth.utils.Session

class AnimeController {
    fun getAnimesByCountry(country: String, page: Int = 1, limit: Int = 9): List<Anime>? {
        val session = Session.sessionFactory.openSession()
        val list =
            session?.createQuery("FROM Anime WHERE country.tag = :tag ORDER BY name", Anime::class.java)
                ?.setParameter("tag", country)
                ?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        session?.close()
        return list
    }
}