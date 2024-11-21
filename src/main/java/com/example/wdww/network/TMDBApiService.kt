package com.example.wdww.network

import com.example.wdww.model.AccountResponse
import com.example.wdww.model.TrendingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApiService {
    @GET("trending/all/day")
    suspend fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("tv/popular")
    suspend fun getPopularTVShows(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<TrendingResponse>

    @GET("account/{account_id}/favorite/tv")
    suspend fun getFavoriteTVShows(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<TrendingResponse>

    @GET("account")
    suspend fun getAccountDetails(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<AccountResponse>

    @GET("discover/movie")
    suspend fun getMoviesByProvider(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") watchProvider: String,
        @Query("watch_region") watchRegion: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("discover/tv")
    suspend fun getTVShowsByProvider(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") watchProvider: String,
        @Query("watch_region") watchRegion: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("discover/multi")
    suspend fun discoverMedia(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("search/multi")
    suspend fun searchMedia(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>
}