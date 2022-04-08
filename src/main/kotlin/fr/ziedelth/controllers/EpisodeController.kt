package fr.ziedelth.controllers

import fr.ziedelth.models.*
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class EpisodeController {
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

    fun getEpisodes(country: String, page: Int = 1, limit: Int = 9): List<Episode>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery(
            "FROM Episode WHERE anime.country.tag = :tag ORDER BY releaseDate DESC, anime.name, season DESC, number DESC, episodeType.id, langType.id",
            Episode::class.java
        )?.setParameter("tag", country)?.setFirstResult((page - 1) * limit)?.setMaxResults(limit)?.list()
        session?.close()
        return list
    }

    fun getEpisodesByAnime(animeId: Long): List<Episode>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery(
            "FROM Episode WHERE anime.id = :animeId ORDER BY releaseDate DESC, anime.name, season DESC, number DESC, episodeType.id, langType.id",
            Episode::class.java
        )?.setParameter("animeId", animeId)?.list()
        session?.close()
        return list
    }
}