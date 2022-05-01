package fr.ziedelth.controllers

import fr.ziedelth.models.Member
import fr.ziedelth.utils.Session
import io.ktor.http.*
import java.security.MessageDigest
import java.util.*

object MemberController {
    // Create regex with minimum length of 4 characters and maximum length of 16 characters, with only letters and numbers
    private val pseudoRegex = Regex("^[a-zA-Z\\d]{4,16}$")

    // Create regex who check is a valid email
    private val emailRegex =
        Regex("^[a-zA-Z\\d.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z\\d](?:[a-zA-Z\\d-]{0,61}[a-zA-Z\\d])?(?:\\.[a-zA-Z\\d](?:[a-zA-Z\\d-]{0,61}[a-zA-Z\\d])?)*$")

    // Check if pseudo is valid
    private fun isPseudoValid(pseudo: String): Boolean {
        return pseudoRegex.matches(pseudo)
    }

    // Check if pseudo is already used
    private fun isPseudoAlreadyUsed(pseudo: String): Boolean {
        val session = Session.sessionFactory.openSession()
        val member = session?.createQuery(
            "FROM Member WHERE pseudo = :pseudo",
            Member::class.java
        )?.setParameter("pseudo", pseudo)?.list()?.firstOrNull()
        session?.close()
        return member != null
    }

    // Check if email is valid
    private fun isEmailValid(email: String): Boolean {
        return emailRegex.matches(email)
    }

    // Check if email is already used
    private fun isEmailAlreadyUsed(email: String): Boolean {
        val session = Session.sessionFactory.openSession()
        val member = session?.createQuery(
            "FROM Member WHERE email = :email",
            Member::class.java
        )?.setParameter("email", email)?.list()?.firstOrNull()
        session?.close()
        return member != null
    }

    // Create sha-224 algorithm
    private fun sha224(value: String): String {
        val md = MessageDigest.getInstance("SHA-224")
        val digest = md.digest(value.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    // Create sha-256 algorithm
    private fun sha256(value: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(value.toByteArray())
        val sb = StringBuilder()
        for (b in digest) sb.append(String.format("%02x", b))
        return sb.toString()
    }

    // Create sha-384 algorithm
    private fun sha384(value: String): String {
        val md = MessageDigest.getInstance("SHA-384")
        val digest = md.digest(value.toByteArray())
        val sb = StringBuilder()
        for (b in digest) sb.append(String.format("%02x", b))
        return sb.toString()
    }

    // Create sha-512 algorithm
    private fun sha512(value: String): String {
        val md = MessageDigest.getInstance("SHA-512")
        val digest = md.digest(value.toByteArray())
        val sb = StringBuilder()
        for (b in digest) sb.append(String.format("%02x", b))
        return sb.toString()
    }

    private fun getMemberByEmail(email: String): Member? {
        val session = Session.sessionFactory.openSession()
        val member = session?.createQuery(
            "FROM Member WHERE email = :email",
            Member::class.java
        )?.setParameter("email", email)?.list()?.firstOrNull()
        session?.close()
        return member
    }

    fun getMemberByToken(token: String): Member? {
        val session = Session.sessionFactory.openSession()
        val member = session?.createQuery(
            "FROM Member WHERE token = :token",
            Member::class.java
        )?.setParameter("token", token)?.list()?.firstOrNull()
        session?.close()
        return member
    }

    fun getMemberByPseudo(pseudo: String): Member? {
        val session = Session.sessionFactory.openSession()
        val member = session?.createQuery(
            "FROM Member WHERE pseudo = :pseudo",
            Member::class.java
        )?.setParameter("pseudo", pseudo)?.list()?.firstOrNull()
        session?.close()
        return member
    }

    private fun hash(random: Int, passwordSalt: String) = when (random) {
        0 -> sha224(passwordSalt)
        1 -> sha256(passwordSalt)
        2 -> sha384(passwordSalt)
        3 -> sha512(passwordSalt)
        else -> sha512(passwordSalt)
    }

    fun register(pseudo: String, email: String, password: String): Member? {
        // If pseudo is not valid or already used, return false
        if (!isPseudoValid(pseudo) || isPseudoAlreadyUsed(pseudo)) return null
        // If email is not valid or already used, return false
        if (!isEmailValid(email) || isEmailAlreadyUsed(email)) return null

        // Generate a uuid
        val salt = UUID.randomUUID().toString()
        // Pick a random int between 0 and 3
        val random = Random().nextInt(4)
        // Salt password
        val passwordSalt = "$password$random$salt"
        // Hash password
        val passwordHash = hash(random, passwordSalt)

        val member = Member(null, Calendar.getInstance(), pseudo, email, false, "$salt$$random$$passwordHash")
        // Save member
        val session = Session.sessionFactory.openSession()
        session.save(member)
        session.close()
        return member
    }

    fun Member.withoutSensitiveInformation(token: Boolean = false) = this.copy(
        timestamp = null,
        email = null,
        emailVerified = null,
        password = null,
        lastLogin = null,
        token = if (token) this.token else null
    )

    fun loginWithCredentials(email: String, password: String): Pair<HttpStatusCode, Any> {
        // If email is not valid or not used, return false
        if (!isEmailValid(email) || !isEmailAlreadyUsed(email)) return Pair(
            HttpStatusCode.BadRequest,
            "Email is not valid or not used"
        )
        // Get member
        val member = getMemberByEmail(email) ?: return Pair(HttpStatusCode.NotFound, "Member not found")
        // Get salt and random
        val split = member.password?.split("$")
        val salt = split?.get(0) ?: return Pair(HttpStatusCode.NoContent, "No salt")
        val random = split[1].toIntOrNull() ?: return Pair(HttpStatusCode.NoContent, "No random")
        val passwordHashed = split[2]
        if (passwordHashed != hash(random, "$password$random$salt")) return Pair(
            HttpStatusCode.Unauthorized,
            "Wrong password"
        )

        member.lastLogin = Calendar.getInstance()
        member.token = UUID.randomUUID().toString()

        // Save the member
        val session = Session.sessionFactory.openSession()
        val transaction = session.beginTransaction()
        session.saveOrUpdate(member)
        transaction.commit()
        session.close()

        return Pair(HttpStatusCode.OK, member.withoutSensitiveInformation(true))
    }

    fun loginWithToken(token: String): Pair<HttpStatusCode, Any> {
        // Get member
        val member = getMemberByToken(token) ?: return Pair(HttpStatusCode.NotFound, "Member not found")

        // If member lastLogin is more than 1 month ago, return false
        if ((member.lastLogin?.time?.time ?: 0) < Calendar.getInstance().time.time - 2592000000) return Pair(
            HttpStatusCode.Unauthorized,
            "Member last login is more than 1 month ago"
        )

        return Pair(HttpStatusCode.OK, member.withoutSensitiveInformation(true))
    }
}