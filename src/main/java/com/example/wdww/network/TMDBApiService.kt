package com.example.wdww.network

import com.example.wdww.model.TrendingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApiService {
    @GET("trending/all/day")
    suspend fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>
}