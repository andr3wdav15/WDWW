/**
 * Retrofit service interface for TMDB authentication endpoints.
 * This interface handles the two-step authentication process required by TMDB:
 * 1. Create a request token
 * 2. Create a session using the approved request token
 * 
 * The authentication flow works as follows:
 * - Get a request token
 * - User approves the token via TMDB website
 * - Exchange the approved token for a session ID
 * - Use the session ID for authenticated requests
 */
package com.example.wdww.network

import com.example.wdww.model.auth.CreateSessionRequest
import com.example.wdww.model.auth.CreateSessionResponse
import com.example.wdww.model.auth.RequestTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Service interface containing TMDB authentication endpoints.
 * These endpoints are separated from the main API service to maintain
 * a clear separation of concerns between authentication and general API operations.
 */
interface TMDBAuthService {
    /**
     * Creates a new request token for initiating the authentication process.
     * The token must be approved by the user via the TMDB website before it
     * can be used to create a session.
     *
     * @param apiKey TMDB API key
     * @return Response containing the request token and its expiration time
     */
    @GET("authentication/token/new")
    suspend fun createRequestToken(
        @Query("api_key") apiKey: String
    ): Response<RequestTokenResponse>

    /**
     * Creates a new session using an approved request token.
     * The session ID returned by this endpoint can be used for
     * authenticated requests to the TMDB API.
     *
     * @param apiKey TMDB API key
     * @param request Request body containing the approved request token
     * @return Response containing the session ID if successful
     */
    @POST("authentication/session/new")
    suspend fun createSession(
        @Query("api_key") apiKey: String,
        @Body request: CreateSessionRequest
    ): Response<CreateSessionResponse>
}