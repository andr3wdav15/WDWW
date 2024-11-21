package com.example.wdww.network

import com.example.wdww.model.CreateSessionRequest
import com.example.wdww.model.CreateSessionResponse
import com.example.wdww.model.DeleteSessionRequest
import com.example.wdww.model.DeleteSessionResponse
import com.example.wdww.model.RequestTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Query

interface TMDBAuthService {
    @GET("authentication/token/new")
    suspend fun createRequestToken(
        @Query("api_key") apiKey: String
    ): Response<RequestTokenResponse>

    @POST("authentication/session/new")
    suspend fun createSession(
        @Query("api_key") apiKey: String,
        @Body request: CreateSessionRequest
    ): Response<CreateSessionResponse>

    @HTTP(method = "DELETE", path = "authentication/session", hasBody = true)
    suspend fun deleteSession(
        @Query("api_key") apiKey: String,
        @Body request: DeleteSessionRequest
    ): Response<DeleteSessionResponse>
} 