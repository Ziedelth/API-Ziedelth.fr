package fr.ziedelth.models

import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.Formula
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.io.Serializable

@Entity
@Table(name = "animes")
data class Anime(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(
        name = "anime_codes",
        joinColumns = [JoinColumn(name = "anime_id")],
        foreignKey = ForeignKey(
            name = "fk_anime_codes_anime_id",
            foreignKeyDefinition = "FOREIGN KEY (anime_id) REFERENCES animes(id) ON DELETE CASCADE"
        )
    )
    @Column(name = "code", nullable = false)
    var codes: MutableList<String>? = null,

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
        name = "anime_genres",
        joinColumns = [JoinColumn(name = "anime_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "genre_id", referencedColumnName = "id")],
        foreignKey = ForeignKey(
            name = "fk_anime_genres_anime_id",
            foreignKeyDefinition = "FOREIGN KEY (anime_id) REFERENCES animes(id) ON DELETE CASCADE"
        ),
        inverseForeignKey = ForeignKey(
            name = "fk_anime_genres_genre_id",
            foreignKeyDefinition = "FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE"
        )
    )
    var genres: MutableList<Genre>? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "country_id",
        nullable = false,
        foreignKey = ForeignKey(
            name = "fk_anime_country_id",
            foreignKeyDefinition = "FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE"
        )
    )
    val country: Country? = null,

    @Column(name = "release_date", nullable = false)
    val releaseDate: String? = null,

    @Column(nullable = false, unique = true)
    val name: String? = null,

    @Transient
    var url: String? = null,

    @Column(nullable = false)
    var image: String? = null,

    @Column(nullable = true)
    var description: String? = null,

    @Formula("(SELECT COUNT(w.member_id) FROM watchlist w WHERE w.anime_id = id)")
    val inWatchlist: Long? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Anime

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , codes = $codes , country = $country , releaseDate = $releaseDate , name = $name , url = $url , image = $image , description = $description )"
    }
}
