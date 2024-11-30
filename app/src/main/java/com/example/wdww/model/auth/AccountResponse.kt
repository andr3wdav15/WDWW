/**
 * Data model class representing the response from TMDB API's account endpoint.
 * This class maps the JSON response from the API to a Kotlin data class,
 * containing user account information such as ID, name, username, and content preferences.
 */
package com.example.wdww.model.auth

import com.google.gson.annotations.SerializedName

/**
 * Data class that holds user account information from TMDB.
 *
 * @property id The unique identifier for the user's TMDB account
 * @property name The user's display name
 * @property username The user's login username
 * @property includeAdult Whether the account is configured to include adult content
 *                       (mapped from 'include_adult' in the JSON response using SerializedName)
 */
data class AccountResponse(
    val id: Int,
    val name: String,
    val username: String,
    @SerializedName("include_adult") val includeAdult: Boolean
)