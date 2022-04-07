package fr.ziedelth.controllers

import fr.ziedelth.models.EpisodeType
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class EpisodeTypeController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(EpisodeType::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getEpisodeTypes(): List<EpisodeType>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery("FROM EpisodeType", EpisodeType::class.java)?.list()
        session?.close()
        return list
    }
}