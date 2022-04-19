package fr.ziedelth.controllers

import fr.ziedelth.models.Member
import fr.ziedelth.utils.Session
import io.ktor.http.*
import java.security.MessageDigest
import java.util.*

class MemberController {
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

    private fun getMember(email: String): Member? {
        val session = Session.sessionFactory.openSession()
        val member = session?.createQuery(
            "FROM Member WHERE email = :email",
            Member::class.java
        )?.setParameter("email", email)?.list()?.firstOrNull()
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

    fun loginWithCredentials(email: String, password: String): Pair<HttpStatusCode, String> {
        // If email is not valid or not used, return false
        if (!isEmailValid(email) || !isEmailAlreadyUsed(email)) return Pair(
            HttpStatusCode.BadRequest,
            "Email is not valid or not used"
        )
        // Get member
        val member = getMember(email) ?: return Pair(HttpStatusCode.NotFound, "Member not found")
        // Get salt and random
        val split = member.password?.split("$")
        val salt = split?.get(0) ?: return Pair(HttpStatusCode.NoContent, "No salt")
        val random = split[1].toIntOrNull() ?: return Pair(HttpStatusCode.NoContent, "No random")
        val passwordHashed = split[2]
        if (passwordHashed != hash(random, "$password$random$salt")) return Pair(
            HttpStatusCode.Unauthorized,
            "Wrong password"
        )
        return Pair(HttpStatusCode.OK, "OK")
    }
}