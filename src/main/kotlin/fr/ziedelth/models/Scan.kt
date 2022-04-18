package fr.ziedelth.models

import org.hibernate.Hibernate
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "scans")
data class Scan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "platform_id", nullable = false)
    val platform: Platform? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "anime_id", nullable = false)
    var anime: Anime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "episode_type_id", nullable = false)
    val episodeType: EpisodeType? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lang_type_id", nullable = false)
    val langType: LangType? = null,

    @Column(name = "release_date", nullable = false)
    val releaseDate: String? = null,

    @Column(nullable = false)
    val number: Long? = null,

    @Column(nullable = false)
    val url: String? = null,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Scan

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , platform = $platform , anime = $anime , episodeType = $episodeType , langType = $langType , releaseDate = $releaseDate , number = $number , url = $url )"
    }
}
