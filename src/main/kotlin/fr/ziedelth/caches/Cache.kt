package fr.ziedelth.caches

const val CACHE_TIME_CHECK = 5 * 60 * 1000

data class Cache<T>(var lastCheck: Long, var value: T) {
    fun hasExpired(): Boolean {
        return System.currentTimeMillis() - lastCheck > CACHE_TIME_CHECK
    }
}
