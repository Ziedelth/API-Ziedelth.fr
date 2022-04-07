package fr.ziedelth.models

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity(name = "Message")
@Table(name = "messages")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    val content: String,

    @Column
    val author: String,

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    val date: Calendar
) : Serializable {
    constructor() : this(null, "", "", Calendar.getInstance())
}
