package fr.ziedelth.controllers

import fr.ziedelth.models.Message
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class MessageController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(Message::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getMessages(): List<Message>? {
        val session = this.sessionFactory?.openSession()
        val messages = session?.createQuery("FROM Message", Message::class.java)?.list()
        session?.close()
        return messages
    }

    fun addMessage(message: Message): Message {
        val session = this.sessionFactory?.openSession()
        val transaction = session?.beginTransaction()
        session?.save(message)
        transaction?.commit()
        session?.close()
        return message
    }
}