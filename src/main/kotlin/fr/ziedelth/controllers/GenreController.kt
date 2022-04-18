package fr.ziedelth.controllers

import fr.ziedelth.models.Genre
import fr.ziedelth.utils.Session

class GenreController {
    fun getGenres(): List<Genre>? {
        val session = Session.jSessionFactory.openSession()
        val list = session?.createQuery("FROM Genre", Genre::class.java)?.list()
        session?.close()
        return list
    }
}