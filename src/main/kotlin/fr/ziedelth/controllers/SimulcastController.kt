package fr.ziedelth.controllers

import fr.ziedelth.caches.SimulcastCache

class SimulcastController {
    fun getSimulcasts(): List<Map<String, Any>>? = SimulcastCache.get()
}