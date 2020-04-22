package ru.korolevss.authorization.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface API {
    @POST("api/v1/authentication")
    suspend fun authenticate(@Body authRequestParams: AuthRequestParams): Response<Token>

    @POST("api/v1/registration")
    suspend fun signIn(@Body authRequestParams: AuthRequestParams): Response<Token>
}