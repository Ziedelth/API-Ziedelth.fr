package fr.ziedelth.controllers

import fr.ziedelth.models.Platform
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class PlatformController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(Platform::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPlatforms(): List<Platform>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery("FROM Platform", Platform::class.java)?.list()
        session?.close()
        return list
    }
}