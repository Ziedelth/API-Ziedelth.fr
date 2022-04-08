package fr.ziedelth.controllers

import fr.ziedelth.models.*
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class AnimeController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(Platform::class.java)
            configuration.addAnnotatedClass(Country::class.java)
            configuration.addAnnotatedClass(Genre::class.java)
            configuration.addAnnotatedClass(Anime::class.java)
            configuration.addAnnotatedClass(EpisodeType::class.java)
            configuration.addAnnotatedClass(LangType::class.java)
            configuration.addAnnotatedClass(Episode::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAnimesByCountry(country: String, page: Int = 1, limit: Int = 9): List<Anime>? {
        val session = this.sessionFactory?.openSession()
        val list =
            session?.createQuery("FROM Anime WHERE country.tag = :tag ORDER BY name", Anime::class.java)
                ?.setParameter("tag", country)
                ?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        session?.close()
        return list
    }
}