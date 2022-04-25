package fr.ziedelth.models

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.*

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

    @Column(name = "token", nullable = true, unique = true)
    var token: String? = null,
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
        return this::class.simpleName + "(id = $id , timestamp = $timestamp , pseudo = $pseudo , email = $email , emailVerified = $emailVerified , password = $password )"
    }
}
