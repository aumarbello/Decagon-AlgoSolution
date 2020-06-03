package user

import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.*

data class User (
    val id: String,
    val username: String,
    val about: String,
    val submitted: Int,
    @SerializedName("submission_count")
    val noOfSubmissions: Int,
    @SerializedName("comment_count")
    val noOfComments: Int,
    @SerializedName("created_at")
    val createdAt: Long
)

data class UserResponse (
    val page: Int,
    val data: List<User>,
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)

interface UserService {
    @GET("article_users/search")
    suspend fun getUsers(@Query("page") page: Int): UserResponse
}

suspend fun mostActiveUsers(threshold: Int): List<String> = withContext(Dispatchers.IO) {
    getUsers(threshold).sortedBy { it.noOfSubmissions }.map { it.username  }.asReversed()
}

suspend fun getUserWithHighCommentCount(threshold: Int): String = withContext(Dispatchers.IO) {
    getUsers(threshold).maxBy { it.noOfComments }!!.username
}

suspend fun getUserNamesSortedByRecordDate(threshold: Int): List<String> = withContext(Dispatchers.IO) {
    getUsers(threshold).sortedBy { it.createdAt }.map { it.username  }.asReversed()
}

private suspend fun getUsers(threshold: Int): List<User> {
    val service = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://jsonmock.hackerrank.com/api/")
        .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build())
        .build()
        .create(UserService::class.java)

    val users = arrayListOf<User>()
    var page = 1
    while (page <= threshold) {
        val response = service.getUsers(page++)
        users.addAll(response.data)

        if (users.size == response.total && page > 0) {
            break
        }
    }

    return users
}