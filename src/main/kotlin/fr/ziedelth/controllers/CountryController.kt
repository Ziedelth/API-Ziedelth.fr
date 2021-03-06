package fr.ziedelth.controllers

import fr.ziedelth.models.Country
import fr.ziedelth.utils.Session

object CountryController {
    fun getCountries(): List<Country>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM Country", Country::class.java)?.list()
        session?.close()
        return list
    }
}