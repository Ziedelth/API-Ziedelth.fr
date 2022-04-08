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
    @JoinColumn(name = "platform_id")
    val platform: Platform? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "anime_id")
    val anime: Anime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "episode_type_id")
    val episodeType: EpisodeType? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lang_type_id")
    val langType: LangType? = null,

    @Column(name = "release_date")
    val releaseDate: String? = null,

    @Column
    val number: Long? = null,

    @Column
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
