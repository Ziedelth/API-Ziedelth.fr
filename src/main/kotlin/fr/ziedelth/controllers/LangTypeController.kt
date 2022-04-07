package fr.ziedelth.controllers

import fr.ziedelth.models.LangType
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class LangTypeController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(LangType::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLangTypes(): List<LangType>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery("FROM LangType", LangType::class.java)?.list()
        session?.close()
        return list
    }
}