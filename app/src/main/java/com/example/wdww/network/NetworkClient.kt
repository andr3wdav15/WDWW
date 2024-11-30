/**
 * Singleton object for configuring and managing Retrofit network instances.
 * This class sets up the HTTP client and Retrofit builder with necessary interceptors,
 * timeouts, and converters for communicating with the TMDB API.
 */
package com.example.wdww.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient

/**
 * Singleton object providing configured Retrofit instances for TMDB API communication.
 * Uses OkHttp for the HTTP client with authentication interceptor.
 */
object NetworkClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "c5479e7394cd551bad2a1af7e9bff8a3"

    /**
     * Authentication interceptor that adds the API key to every request.
     * Modifies each request to include the TMDB API key as a query parameter.
     */
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val originalHttpUrl = original.url

        val url = originalHttpUrl.newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .build()

        val request = original.newBuilder()
            .url(url)
            .build()

        chain.proceed(request)
    }

    /**
     * Configured OkHttpClient with interceptor and timeouts.
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Lazy-initialized Retrofit instance configured with:
     * - TMDB API base URL
     * - Custom OkHttp client
     * - Gson converter for JSON parsing
     */
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Public API service for general TMDB endpoints.
     * Provides access to movie, TV show, and user-specific operations.
     */
    val api: TMDBApiService by lazy {
        retrofit.create(TMDBApiService::class.java)
    }

    /**
     * Public API service specifically for TMDB authentication endpoints.
     * Handles operations like session creation and deletion.
     */
    val authApi: TMDBAuthService by lazy {
        retrofit.create(TMDBAuthService::class.java)
    }
}
