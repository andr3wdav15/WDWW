/**
 * Data model class representing a release alert for upcoming media content.
 * This class stores information about movies or TV shows that the user wants
 * to be notified about when they are released. It contains essential details
 * needed for displaying and tracking the alert.
 */
package com.example.wdww.model.alerts

/**
 * Data class that holds information about a media release alert.
 *
 * @property mediaId The unique identifier of the movie or TV show
 * @property title The title of the media content
 * @property releaseDate The scheduled release date of the media content
 * @property posterPath The URL path to the media's poster image, can be null if no poster is available
 * @property mediaType The type of media content ("movie" or "tv")
 */
data class Alert(
    val mediaId: Int,
    val title: String,
    val releaseDate: String,
    val posterPath: String?,
    val mediaType: String
)
