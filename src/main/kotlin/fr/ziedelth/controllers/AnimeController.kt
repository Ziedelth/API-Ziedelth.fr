package fr.ziedelth.controllers

import fr.ziedelth.models.Anime
import fr.ziedelth.models.Country
import fr.ziedelth.models.Genre
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class AnimeController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(Country::class.java)
            configuration.addAnnotatedClass(Genre::class.java)
            configuration.addAnnotatedClass(Anime::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAnimes(): List<Anime>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery("FROM Anime", Anime::class.java)?.list()
        session?.close()
        return list
    }

    fun getAnimesByCountry(country: String): List<Anime>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery("FROM Anime WHERE country.tag = :tag", Anime::class.java)?.setParameter("tag", country)?.list()
        session?.close()
        return list
    }
}