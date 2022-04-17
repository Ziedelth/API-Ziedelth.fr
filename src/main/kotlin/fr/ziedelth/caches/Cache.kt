package fr.ziedelth.caches

data class Cache<T>(var lastCheck: Long, var value: T)
