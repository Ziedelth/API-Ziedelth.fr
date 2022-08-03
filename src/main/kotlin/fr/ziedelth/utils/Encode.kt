package fr.ziedelth.utils

import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.encoder.Encoder
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.GZIPOutputStream

object Encode {
    private fun base64(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)

    fun gzip(bytes: ByteArray): ByteArray {
        val bos = ByteArrayOutputStream(bytes.size)
        val gzip = GZIPOutputStream(bos)
        gzip.write(bytes)
        gzip.close()
        val compressed = bos.toByteArray()
        bos.close()
        return compressed
    }

    fun gzip(string: String) = base64(gzip(string.toByteArray()))

    fun brotli(bytes: ByteArray): ByteArray {
        Brotli4jLoader.ensureAvailability()
        return Encoder.compress(bytes)
    }

    fun brotli(string: String) = base64(brotli(string.toByteArray()))
}