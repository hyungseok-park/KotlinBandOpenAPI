import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import io.ktor.http.*
import com.typesafe.config.ConfigFactory

fun main() {

    val config = ConfigFactory.load("app.properties")
    val accessToken = config.getString("band.token")
    val apiHost = config.getString("api.host")
    val apiVersion = config.getString("api.version")

    val url = URLBuilder().apply {
        protocol = URLProtocol.HTTPS
        host = apiHost
        path(apiVersion, "bands")
        parameters.append("access_token", accessToken)
    }.buildString()
    val connection = URL(url).openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = reader.readText()
            reader.close()

            println(response)
        } else {
            println("Failed to get a valid response")
        }
    } finally {
        connection.disconnect()
    }
}