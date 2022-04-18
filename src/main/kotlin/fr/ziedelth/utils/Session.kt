package fr.ziedelth.utils

import fr.ziedelth.models.*
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import kotlin.system.exitProcess

object Session {
    lateinit var jSessionFactory: SessionFactory
    lateinit var zSessionFactory: SessionFactory

    fun init() {
        try {
            val jFile = JFile("jhibernate.cfg.xml")
            val zFile = JFile("zhibernate.cfg.xml")

            // If the file doesn't exist, throw an exception
            if (!jFile.exists())
                throw Exception("JHibernate configuration file not found")

            if (!zFile.exists())
                throw Exception("ZHibernate configuration file not found")

            val configuration = Configuration()

            with(configuration) {
                addAnnotatedClass(Platform::class.java)
                addAnnotatedClass(Country::class.java)
                addAnnotatedClass(Genre::class.java)
                addAnnotatedClass(Anime::class.java)
                addAnnotatedClass(EpisodeType::class.java)
                addAnnotatedClass(LangType::class.java)
                addAnnotatedClass(Episode::class.java)
                addAnnotatedClass(Scan::class.java)
                addAnnotatedClass(Member::class.java)

                configure(jFile.file)
                val jServiceRegistry = StandardServiceRegistryBuilder().applySettings(properties).build()
                jSessionFactory = buildSessionFactory(jServiceRegistry)

                configure(zFile.file)
                val zServiceRegistry = StandardServiceRegistryBuilder().applySettings(properties).build()
                zSessionFactory = buildSessionFactory(zServiceRegistry)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(1)
        }
    }
}