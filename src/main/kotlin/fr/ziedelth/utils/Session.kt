package fr.ziedelth.utils

import fr.ziedelth.models.*
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import kotlin.system.exitProcess

object Session {
    lateinit var sessionFactory: SessionFactory

    fun init() {
        try {
            val jFile = JFile("hibernate.cfg.xml")

            // If the file doesn't exist, throw an exception
            if (!jFile.exists())
                throw Exception("Hibernate configuration file not found")

            val configuration = Configuration()

            with(configuration) {
                addAnnotatedClass(Platform::class.java)
                addAnnotatedClass(Country::class.java)
                addAnnotatedClass(Genre::class.java)
                addAnnotatedClass(Anime::class.java)
                addAnnotatedClass(EpisodeType::class.java)
                addAnnotatedClass(LangType::class.java)
                addAnnotatedClass(Episode::class.java)
                addAnnotatedClass(Member::class.java)

                configure(jFile.file)
                val jServiceRegistry = StandardServiceRegistryBuilder().applySettings(properties).build()
                sessionFactory = buildSessionFactory(jServiceRegistry)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(1)
        }
    }
}