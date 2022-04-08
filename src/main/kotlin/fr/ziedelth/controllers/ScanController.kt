package fr.ziedelth.controllers

import fr.ziedelth.models.*
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class ScanController {
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
            configuration.addAnnotatedClass(Scan::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getScans(country: String, page: Int = 1, limit: Int = 9): List<Scan>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.country.tag = :tag ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id",
            Scan::class.java
        )?.setParameter("tag", country)?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        session?.close()
        return list
    }

    fun getScansByAnime(animeId: Long): List<Scan>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery(
            "FROM Scan WHERE anime.id = :animeId ORDER BY releaseDate DESC, anime.name, number DESC, episodeType.id, langType.id",
            Scan::class.java
        )?.setParameter("animeId", animeId)?.list()
        session?.close()
        return list
    }
}