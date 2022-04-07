package fr.ziedelth.controllers

import fr.ziedelth.models.Author
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration

class AuthorController {
    private var sessionFactory: SessionFactory? = null

    init {
        try {
            val configuration = Configuration()
            configuration.configure()
            configuration.addAnnotatedClass(Author::class.java)
            val serviceRegistry = StandardServiceRegistryBuilder().applySettings(configuration.properties).build()
            this.sessionFactory = configuration.buildSessionFactory(serviceRegistry)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAuthors(): List<Author>? {
        val session = this.sessionFactory?.openSession()
        val messages = session?.createQuery("FROM Author", Author::class.java)?.list()
        session?.close()
        return messages
    }

    fun addAuthor(author: Author): Author {
        val session = this.sessionFactory?.openSession()
        val transaction = session?.beginTransaction()
        session?.save(author)
        transaction?.commit()
        session?.close()
        return author
    }
}