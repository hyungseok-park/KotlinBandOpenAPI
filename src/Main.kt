import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun main() {
    val url = URL("https://openapi.band.us/v2.1/bands?access_token=ZQAAATqHjie0aR7mMUyVx7DgwPyJDgGF1Jdt_IUDQXlop4z1l2kEDRhQ0FrKN44tN1WpDY-kXpieBeSnwqJMqO7dNjMWpknXcbg1nxyf-K_jEBJi")

    // Open a connection
    val connection = url.openConnection() as HttpURLConnection

    try {
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode
        println("Response Code: $responseCode")

        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = reader.readText()
            reader.close()

            println("Response Data:")
            println(response)
        } else {
            println("Failed to get a valid response")
        }
    } finally {
        connection.disconnect()
    }
}