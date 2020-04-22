package ru.korolevss.authorization


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.korolevss.authorization.api.API
import ru.korolevss.authorization.api.AuthRequestParams

object Repository {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://server-autorization.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val api: API by lazy {
        retrofit.create(API::class.java)
    }

    suspend fun authenticate(login: String, password: String) =
        api.authenticate(AuthRequestParams(login, password))

    suspend fun signIn(login: String, password: String) =
        api.signIn(AuthRequestParams(login, password))

}