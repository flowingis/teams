package it.flowing.repository

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import it.flowing.model.Surfer
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.util.*

class PersonInputDTO(
    val id: Int,
    val name: String,
    val description: String,
    val avatar_urls: Map<String, String>
)

class Surfers {
    private val client = OkHttpClient()
    private val gson = Gson()

    private val NICKNAMES = mapOf(
        12 to "Marco C",
        17 to "Miki",
        25 to "Lorenzo P",
        27 to "Carla",
        22 to "Nando",
        51 to "Luigi",
        24 to "Violo",
        48 to "Fosco",
        20 to "Orso",
        32 to "Filippo",
        15 to "Antonio",
        36 to "Andrea M",
        38 to "Daniele R",
        58 to "Patrick",
        28 to "Ettore",
        61 to "Massimo",
        19 to "Silvia",
        23 to "Ramona",
        4 to "Giorgio",
        35 to "Minoccheri",
        31 to "Mattia",
        57 to "Stefano",
        14 to "Strazz"
    )

    fun list(): List<Surfer> {
        val request = Request.Builder()
            .url("https://wp.flowing.it/wp-json/wp/v2/users?per_page=100")
            .build()

        val response: Response = client.newCall(request).execute()
        val jsonString = response.body?.string() ?: "[]"

        val listType: Type = object : TypeToken<ArrayList<PersonInputDTO>>() {}.getType()
        val list: List<PersonInputDTO> = gson.fromJson(jsonString, listType)

        return list
            .filter { dto -> dto.description.isNotEmpty() }
            .filter { dto -> !dto.description.contains("surfer: no") }
            .filter { dto -> !dto.description.contains("active: no") }
            .map { dto ->
                Surfer(
                    id = dto.id,
                    name = dto.name,
                    image = dto.avatar_urls["96"] ?: "",
                    nickname = NICKNAMES[dto.id] ?: ""
                )
            }
    }
}