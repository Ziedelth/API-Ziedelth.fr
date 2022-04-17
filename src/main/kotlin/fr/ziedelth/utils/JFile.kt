package fr.ziedelth.utils

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.io.File

class JFile(val name: String, default: String = JsonObject().toString()) {
    private val currentFolder = File(JFile::class.java.protectionDomain.codeSource.location.path).parent
    val file = File(this.currentFolder, this.name)

    init {
        if (!this.file.exists()) {
            this.file.createNewFile()
            // Write default value
            this.write(default)
        }
    }

    // If mail exists
    fun exists() = this.file.exists()
    fun read() = this.file.readText()

    // Read json
    fun readJson() = Gson().fromJson(this.read(), JsonObject::class.java)
    fun write(content: String) = this.file.writeText(content)
}