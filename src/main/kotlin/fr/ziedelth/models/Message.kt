package fr.ziedelth.models

import org.hibernate.Hibernate
import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val date: Calendar = Calendar.getInstance(),

    @ManyToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    val author: Author? = null,

    @Column(nullable = false)
    val content: String = "",
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Message

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , date = $date , author = $author , content = $content )"
    }
}
