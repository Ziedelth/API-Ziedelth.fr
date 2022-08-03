package fr.ziedelth.models

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.io.Serializable

@Entity
@Table(name = "episodes")
data class Episode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "platform_id",
        nullable = false,
        foreignKey = ForeignKey(
            name = "fk_episodes_platforms",
            foreignKeyDefinition = "FOREIGN KEY (platform_id) REFERENCES platforms(id) ON DELETE CASCADE"
        )
    )
    val platform: Platform? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "anime_id",
        nullable = false,
        foreignKey = ForeignKey(
            name = "fk_episodes_animes",
            foreignKeyDefinition = "FOREIGN KEY (anime_id) REFERENCES animes(id) ON DELETE CASCADE"
        )
    )
    var anime: Anime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "episode_type_id",
        nullable = false,
        foreignKey = ForeignKey(
            name = "fk_episodes_episode_types",
            foreignKeyDefinition = "FOREIGN KEY (episode_type_id) REFERENCES episode_types(id) ON DELETE CASCADE"
        )
    )
    val episodeType: EpisodeType? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "lang_type_id",
        nullable = false,
        foreignKey = ForeignKey(
            name = "fk_episodes_lang_types",
            foreignKeyDefinition = "FOREIGN KEY (lang_type_id) REFERENCES lang_types(id) ON DELETE CASCADE"
        )
    )
    val langType: LangType? = null,

    @Column(name = "release_date", nullable = false)
    val releaseDate: String? = null,

    @Column(nullable = false)
    val season: Long? = null,

    @Column(nullable = false)
    val number: Long? = null,

    @Column(name = "episode_id", unique = true, nullable = false)
    val episodeId: String? = null,

    @Column(nullable = true)
    val title: String? = null,

    @Column(nullable = false)
    val url: String? = null,

    @Column(nullable = false)
    val image: String? = null,

    @Column(nullable = false)
    val duration: Long? = null,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Episode

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , platform = $platform , anime = $anime , episodeType = $episodeType , langType = $langType , releaseDate = $releaseDate , season = $season , number = $number , episodeId = $episodeId , title = $title , url = $url , image = $image , duration = $duration )"
    }
}