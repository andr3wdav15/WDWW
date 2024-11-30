/**
 * Data model classes for handling detailed media information from TMDB API.
 * These models represent comprehensive information about movies and TV shows,
 * including metadata, genres, cast, and crew information.
 */
package com.example.wdww.model.media

import com.google.gson.annotations.SerializedName

/**
 * Primary data class containing detailed information about a media item (movie or TV show).
 *
 * @property id Unique identifier for the media item
 * @property title Movie title (null for TV shows)
 * @property name TV show name (null for movies)
 * @property posterPath URL path for the poster image (mapped from 'poster_path')
 * @property backdropPath URL path for the backdrop image (mapped from 'backdrop_path')
 * @property overview Plot summary or description of the media
 * @property genres List of genres associated with the media
 * @property credits Cast and crew information for the media
 */
data class MediaDetails(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    val overview: String?,
    val genres: List<Genre>,
    val credits: Credits
)

/**
 * Data class representing a genre category.
 * Used to classify media items into different categories like "Action", "Drama", etc.
 *
 * @property id Unique identifier for the genre
 * @property name Display name of the genre
 */
data class Genre(
    val id: Int,
    val name: String
)

/**
 * Data class containing both cast and crew information for a media item.
 * Separates the people involved in the production into their respective roles.
 *
 * @property cast List of actors and their characters
 * @property crew List of production crew members and their roles
 */
data class Credits(
    val cast: List<Cast>,
    val crew: List<Crew>
)

/**
 * Data class representing an actor/actress and their role in the media.
 *
 * @property id Unique identifier for the cast member
 * @property name Actor/actress's name
 * @property profilePath URL path to the person's profile image (mapped from 'profile_path')
 * @property character Name of the character played by the actor/actress
 */
data class Cast(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val character: String
)

/**
 * Data class representing a crew member and their role in the media's production.
 *
 * @property id Unique identifier for the crew member
 * @property name Crew member's name
 * @property job Specific job title (e.g., "Director", "Producer")
 * @property department Department they worked in (e.g., "Directing", "Production")
 * @property profilePath URL path to the person's profile image
 */
data class Crew(
    val id: Int,
    val name: String,
    val job: String,
    val department: String,
    val profilePath: String?
)