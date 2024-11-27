package com.example.wdww.network

import com.example.wdww.model.AccountResponse
import com.example.wdww.model.AddMediaToListRequest
import com.example.wdww.model.CreateListRequest
import com.example.wdww.model.CreateListResponse
import com.example.wdww.model.ListResponse
import com.example.wdww.model.ListsResponse
import com.example.wdww.model.MediaDetails
import com.example.wdww.model.StatusResponse
import com.example.wdww.model.TrendingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("discover/tv")
    suspend fun discoverTVShows(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("search/multi")
    suspend fun searchMedia(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String
    ): Response<MediaDetails>

    @GET("tv/{tv_id}")
    suspend fun getTVShowDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String
    ): Response<MediaDetails>

    @GET("{media_type}/{id}")
    suspend fun getMediaDetails(
        @Path("media_type") mediaType: String,
        @Path("id") mediaId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String
    ): Response<MediaDetails>

    @POST("account/{account_id}/favorite")
    suspend fun addToFavorites(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body body: AddToFavoritesRequest
    ): Response<StatusResponse>

    @POST("list")
    suspend fun createList(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body request: CreateListRequest
    ): Response<CreateListResponse>

    @POST("list/{list_id}/add_item")
    suspend fun addToList(
        @Path("list_id") listId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body request: AddMediaToListRequest
    ): Response<StatusResponse>

    @POST("list/{list_id}/remove_item")
    suspend fun removeFromList(
        @Path("list_id") listId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body request: AddMediaToListRequest
    ): Response<StatusResponse>

    @GET("list/{list_id}")
    suspend fun getList(
        @Path("list_id") listId: Int,
        @Query("api_key") apiKey: String
    ): Response<ListResponse>

    @GET("account/{account_id}/lists")
    suspend fun getLists(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<ListsResponse>

    @DELETE("list/{list_id}")
    suspend fun deleteList(
        @Path("list_id") listId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<StatusResponse>

}