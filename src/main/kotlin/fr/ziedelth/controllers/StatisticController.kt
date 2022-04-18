package fr.ziedelth.controllers

import fr.ziedelth.utils.Session
import java.text.SimpleDateFormat
import java.util.*

class StatisticController {
    fun getCount(days: Int): List<Map<String, Any>> {
        val session = Session.jSessionFactory.openSession()
        val list = mutableListOf<Map<String, Any>>()

        for (i in (days - 1) downTo 0) {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            // Format date to yyyy-MM-dd with SimpleDateFormat
            val formatted = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
            val map = mutableMapOf<String, Any>("date" to formatted)

            val episodes = session?.createQuery(
                "SELECT COUNT(id) FROM Episode WHERE FUNCTION('date_format', releaseDate, '%Y-%m-%d') = :date",
                Long::class.javaObjectType
            )?.setParameter("date", formatted)?.uniqueResult()

            map["episodes"] = episodes ?: 0

            val scans = session?.createQuery(
                "SELECT COUNT(id) FROM Scan WHERE FUNCTION('date_format', releaseDate, '%Y-%m-%d') = :date",
                Long::class.javaObjectType
            )?.setParameter("date", formatted)?.uniqueResult()

            map["scans"] = scans ?: 0
            list.add(map)
        }

        session?.close()
        return list
    }
}