package fr.ziedelth.controllers

import fr.ziedelth.caches.SimulcastCache

object SimulcastController {
    fun getSimulcasts(): List<Map<String, Any>>? = SimulcastCache.get()
}