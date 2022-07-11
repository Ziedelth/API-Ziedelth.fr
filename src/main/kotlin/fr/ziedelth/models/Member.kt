package fr.ziedelth.models

import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "members")
data class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "timestamp", nullable = false)
    val timestamp: Calendar? = null,

    @Column(name = "pseudo", nullable = false, unique = true)
    val pseudo: String? = null,

    @Column(name = "email", nullable = false, unique = true)
    val email: String? = null,

    @Column(name = "email_verified", nullable = false)
    val emailVerified: Boolean? = null,

    @Column(name = "password", nullable = false)
    val password: String? = null,

    @Column(name = "last_login", nullable = true)
    var lastLogin: Calendar? = null,

    @Column(name = "last_login_token", nullable = true)
    var lastLoginToken: Calendar? = null,

    @Column(name = "token", nullable = true, unique = true)
    var token: String? = null,

    @Column(name = "role", nullable = false)
    var role: Int = 0,

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
        name = "watchlist",
        joinColumns = [JoinColumn(name = "member_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "anime_id", referencedColumnName = "id", table = "animes")],
        foreignKey = ForeignKey(
            name = "fk_seen_member_id",
            foreignKeyDefinition = "FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE"
        ),
        inverseForeignKey = ForeignKey(
            name = "fk_watchlist_anime_id",
            foreignKeyDefinition = "FOREIGN KEY (anime_id) REFERENCES animes(id) ON DELETE CASCADE"
        )
    )
    var watchlist: MutableList<Anime>? = null,

    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(
        name = "seen",
        joinColumns = [JoinColumn(name = "member_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "episode_id", referencedColumnName = "id", table = "episodes")],
        foreignKey = ForeignKey(
            name = "fk_seen_member_id",
            foreignKeyDefinition = "FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE"
        ),
        inverseForeignKey = ForeignKey(
            name = "fk_seen_episode_id",
            foreignKeyDefinition = "FOREIGN KEY (episode_id) REFERENCES episodes(id) ON DELETE CASCADE"
        )
    )
    var seen: MutableList<Episode>? = null,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Member

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , timestamp = $timestamp , pseudo = $pseudo , email = $email , emailVerified = $emailVerified , password = $password , lastLogin = $lastLogin , lastLoginToken = $lastLoginToken , token = $token , role = $role )"
    }
}
