package fr.ziedelth.controllers

import fr.ziedelth.models.Platform
import fr.ziedelth.utils.Session

object PlatformController {
    fun getPlatforms(): List<Platform>? {
        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery("FROM Platform", Platform::class.java)?.list()
        session?.close()
        return list
    }

    fun deletePlatform(id: Long) {
        val session = Session.sessionFactory.openSession()
        val platform = session?.get(Platform::class.java, id)

        if (platform == null) {
            session?.close()
            return
        }

        session.beginTransaction()

        session.delete(platform)

        AnimeController.getAllWithoutCache()?.filter { anime ->
            val url = anime.url
            if (url.isNullOrBlank()) return@filter false
            val episodes = EpisodeController.getEpisodesByAnime(url)
            val scans = ScanController.getScansByAnime(url)
            return@filter episodes.isNullOrEmpty() && scans.isNullOrEmpty()
        }?.forEach { anime ->
            session.delete(anime)
        }

        session.transaction?.commit()
        session.close()
    }
}