/**
 * Data model class for handling TMDB API response status information.
 * This class is used to parse the standard response format returned by TMDB API
 * endpoints, particularly for operations that modify data (create, update, delete).
 */
package com.example.wdww.model.response

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the status of an API operation.
 * Used to handle both successful and error responses from the TMDB API.
 *
 * @property statusCode Numeric code indicating the specific status (mapped from 'status_code')
 *                     Common codes include:
 *                     - 1: Success
 *                     - 2: Invalid service
 *                     - 3: Authentication failed
 *                     - 7: Invalid API key
 *                     - 34: Resource not found
 * @property statusMessage Human-readable description of the status (mapped from 'status_message')
 * @property success Boolean indicating whether the operation was successful
 */
data class StatusResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_message") val statusMessage: String,
    val success: Boolean
)
