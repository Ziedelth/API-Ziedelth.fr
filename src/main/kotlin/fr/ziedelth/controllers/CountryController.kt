package fr.ziedelth.controllers

import fr.ziedelth.models.Country
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class CountryController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(Country::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCountries(): List<Country>? {
        val session = this.sessionFactory?.openSession()
        val list = session?.createQuery("FROM Country", Country::class.java)?.list()
        session?.close()
        return list
    }
}