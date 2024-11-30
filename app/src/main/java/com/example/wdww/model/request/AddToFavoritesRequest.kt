/**
 * Data model class for making requests to the TMDB API's favorite/unfavorite endpoint.
 * This class represents the request body required to mark or unmark a movie or TV show
 * as a favorite for the authenticated user's account.
 */
package com.example.wdww.model.request

import com.google.gson.annotations.SerializedName

/**
 * Data class representing a request to modify a media item's favorite status.
 * Used with the TMDB API endpoint: /account/{account_id}/favorite
 *
 * @property mediaType Type of media ("movie" or "tv") (mapped from 'media_type')
 * @property mediaId Unique identifier of the media item to favorite/unfavorite (mapped from 'media_id')
 * @property favorite True to mark as favorite, false to remove from favorites (mapped from 'favorite')
 */
data class AddToFavoritesRequest(
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("media_id") val mediaId: Int,
    @SerializedName("favorite") val favorite: Boolean
)
