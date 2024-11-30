/**
 * Data model class for representing basic information about a media item (movie or TV show) from TMDB API.
 * This class serves as a lightweight version of media details, used in lists and search results
 * where complete details are not necessary. It handles both movies and TV shows, with some
 * properties being specific to one type or the other.
 */
package com.example.wdww.model.media

import com.google.gson.annotations.SerializedName

/**
 * Data class containing essential information about a movie or TV show.
 * Properties are nullable or have default values where appropriate to handle
 * varying responses from different TMDB API endpoints.
 *
 * @property id Unique identifier for the media item
 * @property title Movie title (null for TV shows)
 * @property name TV show name (null for movies)
 * @property overview Brief plot summary or description
 * @property posterPath URL path for the poster image (mapped from 'poster_path')
 * @property backdropPath URL path for the backdrop image (mapped from 'backdrop_path')
 * @property releaseDate Release date for movies (mapped from 'release_date')
 * @property firstAirDate First air date for TV shows (mapped from 'first_air_date')
 * @property mediaType Type of media ("movie" or "tv") (mapped from 'media_type')
 * @property voteAverage Average rating from 0-10 (mapped from 'vote_average')
 * @property genreIds List of genre IDs associated with the media (mapped from 'genre_ids')
 * @property originalTitle Original title if in a different language (mapped from 'original_title')
 * @property originalLanguage Original language code (mapped from 'original_language')
 * @property adult Whether the content is adult-only
 * @property popularity Popularity score
 * @property video Whether video content is available
 * @property voteCount Number of votes received (mapped from 'vote_count')
 */
data class MediaItem(
    val id: Int,
    val title: String?,
    val name: String?,
    val overview: String?,
    @SerializedName("poster_path") 
    val posterPath: String?,
    @SerializedName("backdrop_path") 
    val backdropPath: String?,
    @SerializedName("release_date") 
    val releaseDate: String?,
    @SerializedName("first_air_date") 
    val firstAirDate: String?,
    @SerializedName("media_type") 
    val mediaType: String?,
    @SerializedName("vote_average") 
    val voteAverage: Double?,
    @SerializedName("genre_ids") 
    val genreIds: List<Int>? = null,
    @SerializedName("original_title")
    val originalTitle: String? = null,
    @SerializedName("original_language")
    val originalLanguage: String? = null,
    val adult: Boolean = false,
    val popularity: Double = 0.0,
    val video: Boolean = false,
    @SerializedName("vote_count")
    val voteCount: Int = 0
) {
    /**
     * Custom toString implementation that provides a concise representation
     * of the media item, showing only the essential identifying information.
     *
     * @return String containing the item's ID, title/name, and media type
     */
    override fun toString(): String {
        return "MediaItem(id=$id, title=$title, name=$name, mediaType=$mediaType)"
    }
}
