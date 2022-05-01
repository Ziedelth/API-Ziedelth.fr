package fr.ziedelth.controllers

import fr.ziedelth.models.EpisodeType
import fr.ziedelth.utils.Session

object EpisodeTypeController {
    fun getEpisodeTypes(): List<EpisodeType>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM EpisodeType", EpisodeType::class.java)?.list()
        session?.close()
        return list
    }
}