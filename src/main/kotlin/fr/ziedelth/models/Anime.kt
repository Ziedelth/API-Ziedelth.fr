package fr.ziedelth.models

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "anime_codes", joinColumns = [JoinColumn(name = "anime_id")])
    @Column(name = "code")
    val codes: List<String>? = null,

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "anime_genres", joinColumns = [JoinColumn(name = "anime_id", referencedColumnName = "id")], inverseJoinColumns = [JoinColumn(name = "genre_id", referencedColumnName = "id")])
    var genres: List<Genre>? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    val country: Country? = null,

    @Column(name = "release_date")
    val releaseDate: String? = null,

    @Column
    val name: String? = null,

    @Column
    val image: String? = null,

    @Column
    val description: String? = null,
) : Serializable
