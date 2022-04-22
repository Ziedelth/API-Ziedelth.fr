package fr.ziedelth.utils

import java.util.*
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object JMail {
    fun send(address: String, subject: String, message: String) {
        val jFile = JFile("mail.json")

        if (!jFile.exists()) {
            println("Mail config file not found")
            return
        }

        val jObject = jFile.readJson()

        // If mail-host is not set, return
        if (!jObject.has("mail-host")) {
            println("Mail host not set")
            return
        }

        // If mail-port is not set, return
        if (!jObject.has("mail-port")) {
            println("Mail port not set")
            return
        }

        // If mail-username is not set, return
        if (!jObject.has("mail-username")) {
            println("Mail username not set")
            return
        }

        // If mail-password is not set, return
        if (!jObject.has("mail-password")) {
            println("Mail password not set")
            return
        }

        // Get mail-host
        val mailHost = jObject.get("mail-host").asString
        // Get mail-port
        val mailPort = jObject.get("mail-port").asInt
        // Get mail-username
        val mailUsername = jObject.get("mail-username").asString
        // Get mail-password
        val mailPassword = jObject.get("mail-password").asString

        val props = Properties()
        props["mail.smtp.host"] = mailHost
        props["mail.smtp.socketFactory.port"] = mailPort
        props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.port"] = mailPort
        props["mail.smtp.ssl.protocols"] = "TLSv1.2"
        props["mail.smtp.ssl.trust"] = "*"
        props["mail.smtp.starttls.enable"] = "true"

        val session = javax.mail.Session.getInstance(props, object : javax.mail.Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(mailUsername, mailPassword)
        })

        val mimeMessage = MimeMessage(session)
        mimeMessage.sentDate = Date()
        mimeMessage.setFrom(InternetAddress(mailUsername))
        mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address))
        mimeMessage.subject = subject
        mimeMessage.setText(message, "utf-8", "html")
        Transport.send(mimeMessage)
    }
}