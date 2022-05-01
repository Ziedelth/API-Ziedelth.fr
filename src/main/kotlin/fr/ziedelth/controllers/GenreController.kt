package fr.ziedelth.controllers

import fr.ziedelth.models.Genre
import fr.ziedelth.utils.Session

object GenreController {
    fun getGenres(): List<Genre>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM Genre", Genre::class.java)?.list()
        session?.close()
        return list
    }
}