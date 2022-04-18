package fr.ziedelth.controllers

import fr.ziedelth.models.Country
import fr.ziedelth.utils.Session

class CountryController {
    fun getCountries(): List<Country>? {
        val session = Session.jSessionFactory.openSession()
        val list = session?.createQuery("FROM Country", Country::class.java)?.list()
        session?.close()
        return list
    }
}