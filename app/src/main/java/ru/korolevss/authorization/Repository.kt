package ru.korolevss.authorization


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.korolevss.authorization.api.API
import ru.korolevss.authorization.api.AuthRequestParams
import ru.korolevss.authorization.api.InjectAuthTokenInterceptor
import ru.korolevss.authorization.dto.PostModel
import ru.korolevss.authorization.dto.PostRequestDto
import ru.korolevss.authorization.dto.PostType

object Repository {

    private var retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl("https://server-autorization.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    private var api: API = retrofit.create(API::class.java)

    suspend fun authenticate(login: String, password: String) =
        api.authenticate(AuthRequestParams(login, password))

    suspend fun signUp(login: String, password: String) =
        api.signUp(AuthRequestParams(login, password))

    suspend fun createPost(content: String): Response<Void> {
        val postRequestDto = PostRequestDto(
            textOfPost = content,
            postType = PostType.POST
        )
        return api.createPost(postRequestDto)
    }

    suspend fun getPosts() = api.getPosts()

    suspend fun likedByUser(id: Long) = api.likedByUser(id)

    suspend fun dislikedByUser(id: Long) = api.dislikedByUser(id)

    suspend fun repostedByUser(id: Long, content: String): Response<PostModel> {
        val postRequestDto = PostRequestDto(
            textOfPost = content,
            postType = PostType.REPOST
        )
        return api.repostedByUser(id, postRequestDto)
    }

    suspend fun getRecent() = api.getRecent()

    suspend fun getPostsAfter(id: Long) = api.getPostsAfter(id)

    suspend fun getPostsBefore(id: Long) = api.getPostsBefore(id)

    fun createRetrofitWithAuth(authToken: String) {
        val httpLoggerInterceptor = HttpLoggingInterceptor()
        httpLoggerInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(InjectAuthTokenInterceptor(authToken))
            .addInterceptor(httpLoggerInterceptor)
            .build()
        retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://server-autorization.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(API::class.java)
    }

}