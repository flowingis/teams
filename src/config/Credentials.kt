package it.flowing.config

import com.google.gson.Gson
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths

class Credentials(val password: String, val user: String) {

    companion object {
        private const val CREDENTIAL_PATH = "/credentials.json"
        private val gson = Gson()

        @Throws(IOException::class, URISyntaxException::class)
        fun load(): Credentials {
            val resource = Credentials::class.java.getResource(CREDENTIAL_PATH) ?: throw IllegalStateException()
            val uri = resource.toURI() ?: throw IllegalStateException()

            val JSON = String(Files.readAllBytes(Paths.get(uri)))
            
            return gson.fromJson(JSON, Credentials::class.java)
        }
    }
}