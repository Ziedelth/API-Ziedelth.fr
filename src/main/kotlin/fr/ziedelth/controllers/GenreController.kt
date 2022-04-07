package fr.ziedelth.controllers

import fr.ziedelth.models.Genre
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class GenreController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(Genre::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getGenres(): List<Genre>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery("FROM Genre", Genre::class.java)?.list()
        session?.close()
        return list
    }
}