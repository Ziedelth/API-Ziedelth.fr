package fr.ziedelth.models

import jakarta.persistence.*
import org.hibernate.Hibernate
import java.io.Serializable

@Entity
@Table(name = "platforms")
data class Platform(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val name: String? = null,

    @Column(nullable = false)
    val url: String? = null,

    @Column(nullable = false)
    val image: String? = null,

    @Column(nullable = false)
    val color: Long? = null,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Platform

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , url = $url , image = $image , color = $color )"
    }
}
