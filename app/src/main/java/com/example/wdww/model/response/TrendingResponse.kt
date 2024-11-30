/**
 * Data model class for handling trending media responses from TMDB API.
 * This class represents a paginated response containing trending movies and TV shows,
 * which can be filtered by time window (day/week) and media type (all/movie/tv).
 */
package com.example.wdww.model.response

import com.example.wdww.model.media.MediaItem
import com.google.gson.annotations.SerializedName

/**
 * Data class representing a paginated list of trending media items.
 * Used to parse responses from the TMDB trending endpoints (/trending/{media_type}/{time_window}).
 *
 * @property page Current page number in the paginated response
 * @property results List of trending media items for the current page
 * @property totalPages Total number of available pages (mapped from 'total_pages')
 * @property totalResults Total number of trending items across all pages (mapped from 'total_results')
 */
data class TrendingResponse(
    val page: Int,
    val results: List<MediaItem>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)