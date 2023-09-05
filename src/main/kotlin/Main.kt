import com.typesafe.config.Config
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import io.ktor.http.*
import com.typesafe.config.ConfigFactory
import kotlinx.serialization.json.*

val config: Config = ConfigFactory.load("app.properties")
val accessToken: String = config.getString("band.token")
val apiHost: String = config.getString("api.host")

fun main() {
    queryMyInfo()
    queryMyBands()
    queryPosts()
    queryPostDetails()
    createNewPost()
    deletePost()
    queryComments()
    createNewComment()
    deleteComment()
    queryWriteDeletePermissions()
    queryPhotoAlbums()
    queryPhotos()
}

/**
    query my information
    https://developers.band.us/develop/guide/api/get_user_information?lang=en
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
    https://developers.band.us/develop/guide/api/get_user_information?lang=en
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
    query posts in a band
    https://developers.band.us/develop/guide/api/get_user_information?lang=en
 */
fun queryPosts() {
    val apiVersion = "v2"
    val params = mapOf("band_key" to "enter your band key")
    val jsonResult = httpRequest(apiVersion, "band/posts", params) ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val posts = resultData.jsonObject["items"] ?: return
    posts.jsonArray.map {
        println("post_key: ${it.jsonObject["post_key"]}, content: ${it.jsonObject["content"]}")
    }
}

/**
    query a post in detail
    https://developers.band.us/develop/guide/api/get_post?lang=en
 */
fun queryPostDetails() {
    val apiVersion = "v2.1"
    val params = mapOf("band_key" to "enter your band key"
        , "post_key" to  "enter your post key")
    val jsonResult = httpRequest(apiVersion, "band/post", params) ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val post = resultData.jsonObject["post"] ?: return
    val content = post.jsonObject["content"]
    println("content: $content")
}

/**
 * create a new post
 * https://developers.band.us/develop/guide/api/write_post?lang=en
 */
fun createNewPost() {
    val apiVersion = "v2.2"
    val params = mapOf("band_key" to "enter your band key"
        , "content" to  "Hi!"
        , "do_push" to "yes")
    val jsonResult = httpRequest(apiVersion, "band/post/create", params, "POST") ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val postKey = resultData.jsonObject["post_key"] ?: return
    println("postKey: $postKey")
}

/**
 * delete a post from a band
 * https://developers.band.us/develop/guide/api/remove_post?lang=en
 */
fun deletePost() {
    val apiVersion = "v2"
    val params = mapOf("band_key" to "enter your band key"
        , "post_key" to "enter your post key")
    val jsonResult = httpRequest(apiVersion, "band/post/remove", params, "POST") ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val message = resultData.jsonObject["message"] ?: return
    println("message: $message")
}

/**
 * query comments of a post
 * https://developers.band.us/develop/guide/api/get_comments
 */
fun queryComments() {
    val apiVersion = "v2.1"
    val params = mapOf("band_key" to "enter your band key"
        , "post_key" to  "enter your post key")
    val jsonResult = httpRequest(apiVersion, "band/post/comments", params) ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val comments = resultData.jsonObject["items"] ?: return
    comments.jsonArray.map {
        println("comment_key: ${it.jsonObject["comment_key"]}, content: ${it.jsonObject["content"]}")
    }
}

/**
 * create a new comment
 * https://developers.band.us/develop/guide/api/write_comment?lang=en
 */
fun createNewComment() {
    val apiVersion = "v2"
    val params = mapOf("band_key" to "enter your band key"
        , "post_key" to  "enter your post key"
        , "body" to  "Hi! It's a comment."
        )
    val jsonResult = httpRequest(apiVersion, "band/post/comment/create", params, "POST") ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val message = resultData.jsonObject["message"] ?: return
    println("message: $message")
}

/**
 * delete a comment from a post
 * https://developers.band.us/develop/guide/api/remove_comment?lang=en
 */
fun deleteComment() {
    val apiVersion = "v2"
    val params = mapOf("band_key" to "enter your band key"
        , "post_key" to "enter your post key"
        , "comment_key" to "")
    val jsonResult = httpRequest(apiVersion, "band/post/comment/remove", params, "POST") ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val message = resultData.jsonObject["message"] ?: return
    println("message: $message")
}

/**
 * query write/delete permissions
 * https://developers.band.us/develop/guide/api/get_post_permission?lang=en
 */
fun queryWriteDeletePermissions() {
    val apiVersion = "v2"
    val params = mapOf("band_key" to "enter your band key"
        , "permissions" to  "posting,commenting,contents_deletion")
    val jsonResult = httpRequest(apiVersion, "band/permissions", params) ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val permissions = resultData.jsonObject["permissions"] ?: return
    permissions.jsonArray.map {
        println("${it.toString()}")
    }
}

/**
 * query photo albums
 * https://developers.band.us/develop/guide/api/get_albums?lang=en
 */
fun queryPhotoAlbums() {
    val apiVersion = "v2"
    val params = mapOf("band_key" to "enter your band key")
    val jsonResult = httpRequest(apiVersion, "band/albums", params) ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val albums = resultData.jsonObject["items"] ?: return
    albums.jsonArray.map {
        println("photo_album_key: ${it.jsonObject["photo_album_key"]}, name: ${it.jsonObject["name"]}")
    }
}

/**
 * query photos
 * https://developers.band.us/develop/guide/api/get_photos?lang=en
 */
fun queryPhotos() {
    val apiVersion = "v2"
    val params = mapOf("band_key" to "enter your band key"
        , "photo_album_key" to "enter your album key"
    )
    val jsonResult = httpRequest(apiVersion, "band/album/photos", params) ?: return
    val resultData = jsonResult.jsonObject["result_data"] ?: return
    val albums = resultData.jsonObject["items"] ?: return
    albums.jsonArray.map {
        println("photo_key: ${it.jsonObject["photo_key"]}, url: ${it.jsonObject["url"]}")
    }
}

private fun httpRequest(apiVersion: String
                        , api: String
                        , param: Map<String, String> = mapOf()
                        , requestMethod:String = "GET"): JsonElement? {

    val url = URLBuilder().apply {
        protocol = URLProtocol.HTTPS
        host = apiHost
        path(apiVersion, api)
        parameters.append("access_token", accessToken)
        param.forEach {
            parameters.append(it.key, it.value)
        }
    }.buildString()

    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = requestMethod

    try {
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