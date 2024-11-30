/**
 * Retrofit service interface for the TMDB (The Movie Database) API.
 * This interface defines all the endpoints used by the application for fetching
 * and managing movie/TV show data, user preferences, lists, and search functionality.
 * All methods are suspending functions for use with Kotlin coroutines.
 */
package com.example.wdww.network

import com.example.wdww.model.auth.AccountResponse
import com.example.wdww.model.list.AddMediaToListRequest
import com.example.wdww.model.list.CreateListRequest
import com.example.wdww.model.list.CreateListResponse
import com.example.wdww.model.list.ListResponse
import com.example.wdww.model.list.ListsResponse
import com.example.wdww.model.media.MediaDetails
import com.example.wdww.model.request.AddToFavoritesRequest
import com.example.wdww.model.response.StatusResponse
import com.example.wdww.model.response.TrendingResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Service interface containing all TMDB API endpoints used by the application.
 * Organized into several categories:
 * - Trending and Popular Content
 * - Movie Discovery
 * - TV Show Discovery
 * - User Account Operations
 * - List Management
 * - Search Functionality
 */
interface TMDBApiService {
    /**
     * Fetches trending movies and TV shows for the day.
     * @param apiKey TMDB API key
     * @param page Page number for pagination
     * @return Paginated list of trending media items
     */
    @GET("trending/all/day")
    suspend fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Gets upcoming movie releases.
     * @param apiKey TMDB API key
     * @param page Page number for pagination
     * @return Paginated list of upcoming movies
     */
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Retrieves currently playing movies in theaters.
     * @param apiKey TMDB API key
     * @param page Page number for pagination
     * @return Paginated list of now playing movies
     */
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Gets user's favorite movies.
     * @param accountId User's TMDB account ID
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @return Paginated list of user's favorite movies
     */
    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<TrendingResponse>

    /**
     * Gets user's favorite TV shows.
     * @param accountId User's TMDB account ID
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @return Paginated list of user's favorite TV shows
     */
    @GET("account/{account_id}/favorite/tv")
    suspend fun getFavoriteTVShows(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<TrendingResponse>

    /**
     * Retrieves user account details.
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @return User's account information
     */
    @GET("account")
    suspend fun getAccountDetails(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<AccountResponse>

    /**
     * Discovers movies by streaming provider.
     * @param apiKey TMDB API key
     * @param watchProvider Provider ID (e.g., Netflix, Amazon)
     * @param watchRegion Region code for availability
     * @param page Page number for pagination
     * @return Paginated list of movies available on the specified provider
     */
    @GET("discover/movie")
    suspend fun getMoviesByProvider(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") watchProvider: String,
        @Query("watch_region") watchRegion: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Discovers TV shows by streaming provider.
     * @param apiKey TMDB API key
     * @param watchProvider Provider ID
     * @param watchRegion Region code
     * @param languages Language filter (default: English)
     * @param page Page number
     * @return Paginated list of TV shows on the provider
     */
    @GET("discover/tv")
    suspend fun getTVShowsByProvider(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") watchProvider: String,
        @Query("watch_region") watchRegion: String,
        @Query("with_languages") languages: String = "en",
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Discovers movies by genre.
     * @param apiKey TMDB API key
     * @param genreId Genre identifier
     * @param page Page number
     * @return Paginated list of movies in the specified genre
     */
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Discovers TV shows by genre.
     * @param apiKey TMDB API key
     * @param genreId Genre identifier
     * @param languages Language filter (default: English)
     * @param page Page number
     * @return Paginated list of TV shows in the genre
     */
    @GET("discover/tv")
    suspend fun discoverTVShows(
        @Query("api_key") apiKey: String,
        @Query("with_genres") genreId: String,
        @Query("with_languages") languages: String = "en",
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Gets popular English-language TV shows with advanced filtering.
     * @param apiKey TMDB API key
     * @param language Content language
     * @param sortBy Sort criteria
     * @param originalLanguage Original language filter
     * @param includeAdult Adult content flag
     * @param includeNullDates Include shows without air dates
     * @param minVotes Minimum vote count threshold
     * @param page Page number
     * @return Filtered list of popular TV shows
     */
    @GET("discover/tv")
    suspend fun getPopularEnglishTVShows(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("with_original_language") originalLanguage: String = "en",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_null_first_air_dates") includeNullDates: Boolean = false,
        @Query("vote_count.gte") minVotes: Int = 100,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Searches for movies and TV shows.
     * @param apiKey TMDB API key
     * @param query Search terms
     * @param page Page number
     * @return Search results matching the query
     */
    @GET("search/multi")
    suspend fun searchMedia(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<TrendingResponse>

    /**
     * Gets detailed information about a specific media item.
     * @param mediaType Type ("movie" or "tv")
     * @param mediaId Item identifier
     * @param apiKey TMDB API key
     * @param appendToResponse Additional data to include
     * @return Detailed media information
     */
    @GET("{media_type}/{id}")
    suspend fun getMediaDetails(
        @Path("media_type") mediaType: String,
        @Path("id") mediaId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") appendToResponse: String
    ): Response<MediaDetails>

    /**
     * Adds/removes a media item to/from favorites.
     * @param accountId User's account ID
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @param body Request containing media details
     * @return Operation status
     */
    @POST("account/{account_id}/favorite")
    suspend fun addToFavorites(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body body: AddToFavoritesRequest
    ): Response<StatusResponse>

    /**
     * Creates a new custom list.
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @param request List creation details
     * @return Created list information
     */
    @POST("list")
    suspend fun createList(
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body request: CreateListRequest
    ): Response<CreateListResponse>

    /**
     * Adds a media item to a custom list.
     * @param listId Target list identifier
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @param request Media item to add
     * @return Operation status
     */
    @POST("list/{list_id}/add_item")
    suspend fun addToList(
        @Path("list_id") listId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body request: AddMediaToListRequest
    ): Response<StatusResponse>

    /**
     * Removes a media item from a custom list.
     * @param listId Target list identifier
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @param request Media item to remove
     * @return Operation status
     */
    @POST("list/{list_id}/remove_item")
    suspend fun removeFromList(
        @Path("list_id") listId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String,
        @Body request: AddMediaToListRequest
    ): Response<StatusResponse>

    /**
     * Gets contents of a specific list.
     * @param listId List identifier
     * @param apiKey TMDB API key
     * @return List contents and metadata
     */
    @GET("list/{list_id}")
    suspend fun getList(
        @Path("list_id") listId: Int,
        @Query("api_key") apiKey: String
    ): Response<ListResponse>

    /**
     * Gets all lists created by a user.
     * @param accountId User's account ID
     * @param apiKey TMDB API key
     * @param sessionId Active session ID
     * @return User's custom lists
     */
    @GET("account/{account_id}/lists")
    suspend fun getLists(
        @Path("account_id") accountId: Int,
        @Query("api_key") apiKey: String,
        @Query("session_id") sessionId: String
    ): Response<ListsResponse>

    /**
     * Gets popular movies with filtering options.
     * @param apiKey TMDB API key
     * @param page Page number for pagination
     * @return Paginated list of popular movies excluding theater-only releases
     */
    @GET("discover/movie")
    suspend fun getFilteredPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("watch_region") watchRegion: String = "CA",
        @Query("with_watch_providers") watchProviders: String = "8|337|350|230|531",  // Netflix, Disney+, Apple TV, Crave, Paramount+
        @Query("vote_count.gte") minVotes: Int = 100,
        @Query("page") page: Int
    ): Response<TrendingResponse>
}