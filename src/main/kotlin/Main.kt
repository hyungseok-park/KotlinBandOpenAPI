import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import io.ktor.http.*
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

val config = ConfigFactory.load("app.properties")
val accessToken = config.getString("band.token")
val apiHost = config.getString("api.host")

fun main() {
    queryMyInfo()
    queryMyBands()
}

/**
    query my information
    https://developers.band.us/develop/guide/api/get_user_information
 */
fun queryMyInfo() {
    val apiVersion = "v2"
    val jsonResult = httpRequest(apiVersion, "profile") ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val myName = resultData.jsonObject["name"]
    val profileImg = resultData.jsonObject["profile_image_url"]
    println("myName: $myName, profileImg: $profileImg")
}

/**
query my BANDs
https://developers.band.us/develop/guide/api/get_user_information
 */
fun queryMyBands() {
    val apiVersion = "v2.1"
    val jsonResult = httpRequest(apiVersion, "bands") ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val bands = resultData.jsonObject["bands"] ?: return
    bands.jsonArray.map {
        println("band: ${it.jsonObject["name"]}, band key: ${it.jsonObject["band_key"]}")
    }
}

/**
query posts in a BAND
https://developers.band.us/develop/guide/api/get_user_information
 */
fun queryPosts() {
    val apiVersion = "v2"
    val jsonResult = httpRequest(apiVersion, "band/posts", mapOf<String, String>()) ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val bands = resultData.jsonObject["bands"] ?: return
    bands.jsonArray.map {
        println("band: ${it.jsonObject["name"]}")
    }
}




private fun httpRequest(apiVersion: String, api: String): JsonElement? {
    val url = URLBuilder().apply {
        protocol = URLProtocol.HTTPS
        host = apiHost
        path(apiVersion, api)
        parameters.append("access_token", accessToken)
    }.buildString()
    val connection = URL(url).openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonElement = Json.parseToJsonElement(reader.readText())
            reader.close()
            return jsonElement
        } else {
            println("Failed to get a valid response")
            return null
        }
    } finally {
        connection.disconnect()
    }
}