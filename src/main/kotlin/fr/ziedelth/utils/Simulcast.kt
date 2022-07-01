package fr.ziedelth.utils

import java.util.*

object Simulcast {
    fun getSimulcast(calendar: Calendar?): String? {
        if (calendar == null) return null
        val seasons = arrayOf("Hiver", "Printemps", "Eté", "Automne")
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val index = month / 3
        val season = seasons[index]
        return "$season $year"
    }

    fun getCurrentSimulcast() = getSimulcast(Calendar.getInstance())
}