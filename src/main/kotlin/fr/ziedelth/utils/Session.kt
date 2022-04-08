package fr.ziedelth.utils

import fr.ziedelth.models.*
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import java.io.File

object Session {
    private val currentFolder = File(Session::class.java.protectionDomain.codeSource.location.path).parent
    lateinit var sessionFactory: SessionFactory

    private fun getFile(name: String) = File(this.currentFolder, name)

    fun init() {
        try {
            val configuration = Configuration()
            configuration.configure(getFile("hibernate.cfg.xml"))
            configuration.addAnnotatedClass(Platform::class.java)
            configuration.addAnnotatedClass(Country::class.java)
            configuration.addAnnotatedClass(Genre::class.java)
            configuration.addAnnotatedClass(Anime::class.java)
            configuration.addAnnotatedClass(EpisodeType::class.java)
            configuration.addAnnotatedClass(LangType::class.java)
            configuration.addAnnotatedClass(Episode::class.java)
            configuration.addAnnotatedClass(Scan::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}