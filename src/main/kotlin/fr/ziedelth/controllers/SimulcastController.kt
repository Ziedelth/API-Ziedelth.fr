package fr.ziedelth.controllers

import fr.ziedelth.utils.ISO8601
import fr.ziedelth.utils.Session
import fr.ziedelth.utils.Simulcast

class SimulcastController {
    fun getSimulcasts(): List<Map<String, Any>>? {
        val session = Session.sessionFactory.openSession()
        val list =
            session?.createQuery(
                "SELECT DISTINCT releaseDate FROM Episode ORDER BY releaseDate ASC",
                String::class.java
            )
                ?.list()?.toSet()
        session?.close()
        return list?.mapNotNull { Simulcast.getSimulcast(ISO8601.fromUTCDate(it)) }?.toSet()
            ?.mapIndexed { index, simulcast -> mapOf("id" to index + 1, "simulcast" to simulcast) }
    }
}