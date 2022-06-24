package fr.ziedelth.controllers

import fr.ziedelth.caches.AnimeCache
import fr.ziedelth.models.Episode
import fr.ziedelth.utils.Session
import io.ktor.http.*

object WatchlistController {
    fun getWatchlistEpisodes(pseudo: String, page: Int = 1, limit: Int = 9): List<Episode>? {
        val member = MemberController.getMemberByPseudo(pseudo) ?: return null

        val session = Session.sessionFactory.openSession()
        val list = session?.createQuery(
            "FROM Episode WHERE anime.id IN :watchlist ORDER BY releaseDate DESC, anime.name, season DESC, number DESC, episodeType.id, langType.id, id DESC",
            Episode::class.java
        )?.setParameter("watchlist", member.watchlist?.map { it.id })?.setFirstResult((page - 1) * limit)
            ?.setMaxResults(limit)?.list()
        list?.forEach { AnimeCache.setUrl(it.anime) }
        session?.close()
        return list
    }

    fun addToWatchlist(token: String, id: Long): HttpStatusCode {
        val member = MemberController.getMemberByToken(token) ?: return HttpStatusCode.NotFound
        val anime = AnimeController.getAnimeById(id) ?: return HttpStatusCode.NotFound

        val session = Session.sessionFactory.openSession()
        session?.beginTransaction()
        member.watchlist?.add(anime)
        session?.saveOrUpdate(member)
        session?.transaction?.commit()
        session?.close()

        return HttpStatusCode.OK
    }

    fun removeToWatchlist(token: String, id: Long): HttpStatusCode {
        val member = MemberController.getMemberByToken(token) ?: return HttpStatusCode.NotFound
        val anime = AnimeController.getAnimeById(id) ?: return HttpStatusCode.NotFound

        val session = Session.sessionFactory.openSession()
        session?.beginTransaction()
        member.watchlist?.remove(anime)
        session?.saveOrUpdate(member)
        session?.transaction?.commit()
        session?.close()

        return HttpStatusCode.OK
    }
}