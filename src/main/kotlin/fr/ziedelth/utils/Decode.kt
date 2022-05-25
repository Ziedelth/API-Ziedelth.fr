package fr.ziedelth.utils

import com.aayushatharva.brotli4j.Brotli4jLoader
import com.aayushatharva.brotli4j.decoder.Decoder
import java.io.ByteArrayInputStream
import java.util.*
import java.util.zip.GZIPInputStream

object Decode {
    private fun base64(string: String): ByteArray = Base64.getDecoder().decode(string)

    fun gzip(string: String): String {
        val gzip = GZIPInputStream(ByteArrayInputStream(base64(string)))
        val compressed = gzip.readBytes()
        gzip.close()
        return String(compressed)
    }

    fun brotli(string: String): String {
        Brotli4jLoader.ensureAvailability()
        return String(Decoder.decompress(base64(string)).decompressedData)
    }
}