/**
 * Singleton object for configuring and managing Retrofit network instances.
 * This class sets up the HTTP client and Retrofit builder with necessary interceptors,
 * timeouts, and converters for communicating with the TMDB API.
 */
package com.example.wdww.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor

/**
 * Singleton object providing configured Retrofit instances for TMDB API communication.
 * Uses OkHttp for the HTTP client with logging and authentication interceptors.
 */
object NetworkClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val API_KEY = "c5479e7394cd551bad2a1af7e9bff8a3"

    /**
     * Logging interceptor for debugging network requests and responses.
     * Set to BODY level to log complete request/response details.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

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
     * Configured OkHttpClient with interceptors and timeouts.
     * - Includes logging for debugging
     * - Adds authentication to requests
     * - Sets connection, read, and write timeouts to 15 seconds
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
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
