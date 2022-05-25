package fr.ziedelth.utils

import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.encoder.Encoder
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.GZIPOutputStream

object Encode {
    private fun base64(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    fun gzip(string: String): String {
        val bos = ByteArrayOutputStream(string.length)
        val gzip = GZIPOutputStream(bos)
        gzip.write(string.toByteArray())
        gzip.close()
        val compressed = bos.toByteArray()
        bos.close()
        return base64(compressed)
    }

    fun brotli(string: String): String {
        Brotli4jLoader.ensureAvailability()
        return base64(Encoder.compress(string.toByteArray()))
    }
}