package fr.ziedelth.controllers

import fr.ziedelth.models.Anime
import fr.ziedelth.models.Member
import fr.ziedelth.utils.JMail
import fr.ziedelth.utils.Session
import java.security.MessageDigest
import java.util.*

class MemberController {
    // Create regex with minimum length of 4 characters and maximum length of 16 characters, with only letters and numbers
    private val pseudoRegex = Regex("^[a-zA-Z\\d]{4,16}$")
    // Create regex who check is a valid email
    private val emailRegex = Regex("^[a-zA-Z\\d.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z\\d](?:[a-zA-Z\\d-]{0,61}[a-zA-Z\\d])?(?:\\.[a-zA-Z\\d](?:[a-zA-Z\\d-]{0,61}[a-zA-Z\\d])?)*$")

    // Check if pseudo is valid
    private fun isPseudoValid(pseudo: String): Boolean {
        return pseudoRegex.matches(pseudo)
    }

    // Check if pseudo is already used
    private fun isPseudoAlreadyUsed(pseudo: String): Boolean {
        val session = Session.zSessionFactory.openSession()
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
        val session = Session.zSessionFactory.openSession()
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
        val passwordHash = when (random) {
            0 -> sha224(passwordSalt)
            1 -> sha256(passwordSalt)
            2 -> sha384(passwordSalt)
            3 -> sha512(passwordSalt)
            else -> sha512(passwordSalt)
        }

        // Send email
//        JMail.send(email, "Confirmation de votre compte", """
//            <div style="margin: 0;">
//                <div style="display: flex">
//                    <img src="https://ziedelth.fr/images/favicon.jpg" style="width: 64px; border-radius: 8px" alt="Icon">
//                    <div style="margin-left: 0.5rem">
//                        <p style="margin-bottom: 0; font-weight: bold">$pseudo,</p>
//                        <p style="margin-top: 0">Merci de votre inscription</p>
//                    </div>
//                </div>
//                <div style="margin-top: 1vh">
//                    <p style="margin-top: 0; margin-bottom: 10px">Veuillez cliquez sur le lien suivant pour terminer votre inscription :</p>
//                    <a href="#" style="text-decoration: underline; text-decoration-color: black; color: black">Confirmer mon inscription</a>
//                    <p style="margin-bottom: 0">Votre inscription ne sera effective que si vous cliquez sur le lien de confirmation ci-dessus.</p>
//                    <i>Vous ne pourrez vous connecter que lorsque votre adresse mail sera confirmée.</i>
//                    <p style="margin-bottom: 0">Cordialement,</p>
//                    <p style="margin-top: 0">Ziedelth.fr</p>
//                    <div>
//                        <i>Si vous n'êtes pas à l'origine de cette demande, vous pouvez ignorer ce mail.</i>
//                        <br>
//                        <i>Cette action n'est valable que 10 minutes.</i>
//                    </div>
//
//                    <div style="margin-top: 0.5vh"><i>Ce mail est envoyé automatiquement, merci de ne pas y répondre.</i></div>
//                </div>
//            </div>
//        """.trimIndent())

        val member = Member(null, Calendar.getInstance(), pseudo, email, false, "$salt$$random$$passwordHash")
        // Save member
        val session = Session.zSessionFactory.openSession()
        session.save(member)
        session.close()
        return member
    }
}