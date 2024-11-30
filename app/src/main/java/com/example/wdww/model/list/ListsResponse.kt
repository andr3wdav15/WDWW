/**
 * Data model classes for handling paginated responses of user's custom lists from TMDB API.
 * These models represent the structure of the response when retrieving all lists
 * associated with a user's account, with support for pagination.
 */
package com.example.wdww.model.list

import com.google.gson.annotations.SerializedName

/**
 * Response model for retrieving a paginated collection of user's lists.
 * This class wraps the paginated results of list information.
 *
 * @property page Current page number in the paginated response
 * @property results List of custom lists in the current page
 * @property totalPages Total number of available pages (mapped from 'total_pages')
 * @property totalResults Total number of lists across all pages (mapped from 'total_results')
 */
data class ListsResponse(
    val page: Int,
    val results: List<ListInfo>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

/**
 * Data model representing summary information about a user's custom list.
 * Contains metadata about the list without including its media items.
 *
 * @property id Unique identifier for the list
 * @property name Name of the list
 * @property description Optional description of the list's contents or purpose
 * @property itemCount Number of items in the list (mapped from 'item_count')
 * @property favoriteCount Number of users who have favorited this list (mapped from 'favorite_count')
 * @property language Language code for the list content (mapped from 'iso_639_1')
 * @property listType Type of the list (mapped from 'list_type')
 */
data class ListInfo(
    val id: Int,
    val name: String,
    val description: String?,
    @SerializedName("item_count")
    val itemCount: Int,
    @SerializedName("favorite_count")
    val favoriteCount: Int,
    @SerializedName("iso_639_1")
    val language: String,
    @SerializedName("list_type")
    val listType: String
)
