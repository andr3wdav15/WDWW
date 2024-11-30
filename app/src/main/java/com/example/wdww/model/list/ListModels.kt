/**
 * Data model classes for handling TMDB API list operations.
 * These models support creating custom lists, adding/removing media items,
 * and retrieving list information with paginated results.
 * 
 * Key Components:
 * - CreateListRequest: Create new custom lists
 * - AddMediaToListRequest: Add/remove items from lists
 * - ListResponse: Retrieve list contents and metadata
 * 
 * Note: All models are designed to match TMDB API specifications,
 * using SerializedName annotations where necessary for proper JSON mapping.
 */
package com.example.wdww.model.list

import com.example.wdww.model.media.MediaItem
import com.google.gson.annotations.SerializedName

/**
 * Request model for creating a new custom list.
 * Used when a user wants to create a new collection of media items.
 *
 * @property name The name of the new list
 * @property description A description of the list's purpose or contents
 * @property language The language code for the list (defaults to "en" for English)
 */
data class CreateListRequest(
    val name: String,
    val description: String,
    val language: String = "en"
)

/**
 * Response model for the create list endpoint.
 * Contains information about the success of the list creation.
 *
 * @property statusMessage A message describing the result of the operation
 * @property success Indicates if the list creation was successful
 * @property listID The unique identifier for the newly created list
 */
data class CreateListResponse(
    val statusMessage: String,
    val success: Boolean,
    val listID: Int
)

/**
 * Request model for adding or removing a media item from a list.
 * Used for both adding and removing movies or TV shows from custom lists.
 * The same model is used for both operations since they require the same data.
 *
 * @property mediaId The unique identifier of the media item (serialized as "media_id" for TMDB API)
 */
data class AddMediaToListRequest(
    @SerializedName("media_id")
    val mediaId: Int
)

/**
 * Response model for retrieving list details and contents.
 * Includes both list metadata and a paginated collection of media items.
 *
 * @property id The unique identifier of the list
 * @property name The name of the list
 * @property description The description of the list
 * @property itemCount Total number of items in the list (mapped from 'item_count')
 * @property language The language code of the list (mapped from 'iso_639_1')
 * @property favoriteCount Number of users who have favorited this list (mapped from 'favorite_count')
 * @property results List of media items in the current page (mapped from 'items')
 * @property page Current page number
 * @property totalPages Total number of available pages (mapped from 'total_pages')
 * @property totalResults Total number of items across all pages (mapped from 'total_results')
 */
data class ListResponse(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("item_count")
    val itemCount: Int,
    @SerializedName("iso_639_1")
    val language: String,
    @SerializedName("favorite_count")
    val favoriteCount: Int,
    @SerializedName("items")
    val results: List<MediaItem>,
    val page: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
