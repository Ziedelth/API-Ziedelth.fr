package fr.ziedelth.controllers

import fr.ziedelth.models.LangType
import fr.ziedelth.utils.Session

class LangTypeController {
    fun getLangTypes(): List<LangType>? {
        val session = Session.jSessionFactory.openSession()
        val list = session?.createQuery("FROM LangType", LangType::class.java)?.list()
        session?.close()
        return list
    }
}