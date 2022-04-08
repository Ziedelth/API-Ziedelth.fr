package fr.ziedelth.controllers

import fr.ziedelth.models.Platform
import fr.ziedelth.utils.Session

class PlatformController {
    fun getPlatforms(): List<Platform>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM Platform", Platform::class.java)?.list()
        session?.close()
        return list
    }
}