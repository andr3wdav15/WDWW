/**
 * Data model classes for handling TMDB API authentication flow.
 * This file contains request and response models for the three-step authentication process:
 * 1. Request Token generation
 * 2. Session creation
 * 3. Session deletion
 */
package com.example.wdww.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Response model for the request token endpoint.
 * A request token is required as the first step in the authentication process.
 *
 * @property success Indicates if the token request was successful
 * @property expiresAt Timestamp when the request token expires (mapped from 'expires_at')
 * @property requestToken The generated request token (mapped from 'request_token')
 */
data class RequestTokenResponse(
    val success: Boolean,
    @SerializedName("expires_at") val expiresAt: String,
    @SerializedName("request_token") val requestToken: String
)

/**
 * Request model for creating a new session.
 * After the user approves the request token, it's used to create a session.
 *
 * @property requestToken The approved request token (mapped from 'request_token')
 */
data class CreateSessionRequest(
    @SerializedName("request_token") val requestToken: String
)

/**
 * Response model for the create session endpoint.
 * Contains the session ID that will be used for authenticated requests.
 *
 * @property success Indicates if the session creation was successful
 * @property sessionId The generated session ID (mapped from 'session_id')
 */
data class CreateSessionResponse(
    val success: Boolean,
    @SerializedName("session_id") val sessionId: String
)