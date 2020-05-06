package ru.korolevss.authorization


import android.graphics.Bitmap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.korolevss.authorization.api.API
import ru.korolevss.authorization.api.AttachmentModel
import ru.korolevss.authorization.api.AuthRequestParams
import ru.korolevss.authorization.api.InjectAuthTokenInterceptor
import ru.korolevss.authorization.dto.PostModel
import ru.korolevss.authorization.dto.PostRequestDto
import ru.korolevss.authorization.dto.PostType
import java.io.ByteArrayOutputStream

const val BASE_URL = "https://server-autorization.herokuapp.com"

object Repository {

    private var retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


    private var api: API = retrofit.create(API::class.java)

    suspend fun authenticate(login: String, password: String) =
        api.authenticate(AuthRequestParams(login, password))

    suspend fun signUp(login: String, password: String) =
        api.signUp(AuthRequestParams(login, password))

    suspend fun createPost(content: String, attachmentModelId: String? = null): Response<Void> {
        val postRequestDto = PostRequestDto(
            textOfPost = content,
            postType = PostType.POST,
            attachmentId = attachmentModelId
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

    suspend fun upload(bitmap: Bitmap): Response<AttachmentModel> {
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val reqFIle =
            RequestBody.create("image/jpeg".toMediaTypeOrNull(), bos.toByteArray())
        val body =
            MultipartBody.Part.createFormData("file", "image.jpg", reqFIle)
        return api.uploadImage(body)
    }

}