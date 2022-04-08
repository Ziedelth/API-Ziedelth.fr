package fr.ziedelth.models

import org.hibernate.Hibernate
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "animes")
data class Anime(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "anime_codes", joinColumns = [JoinColumn(name = "anime_id")])
    @Column(name = "code", nullable = false)
    var codes: List<String>? = null,

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
        name = "anime_genres",
        joinColumns = [JoinColumn(name = "anime_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "genre_id", referencedColumnName = "id")]
    )
    var genres: List<Genre>? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    val country: Country? = null,

    @Column(name = "release_date", nullable = false)
    val releaseDate: String? = null,

    @Column(nullable = false, unique = true)
    val name: String? = null,

    @Column(nullable = false)
    val image: String? = null,

    @Column(nullable = true)
    val description: String? = null,
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
        return this::class.simpleName + "(id = $id , codes = $codes , country = $country , releaseDate = $releaseDate , name = $name , image = $image , description = $description )"
    }
}
