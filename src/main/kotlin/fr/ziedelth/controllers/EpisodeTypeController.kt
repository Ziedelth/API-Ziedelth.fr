package fr.ziedelth.controllers

import fr.ziedelth.models.EpisodeType
import fr.ziedelth.utils.Session

class EpisodeTypeController {
    fun getEpisodeTypes(): List<EpisodeType>? {
        val session = Session.jSessionFactory.openSession()
        val list = session?.createQuery("FROM EpisodeType", EpisodeType::class.java)?.list()
        session?.close()
        return list
    }
}