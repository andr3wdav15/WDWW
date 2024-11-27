package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class MediaItem(
    val id: Int,
    val title: String?,
    val name: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("genre_ids") val genreIds: List<Int>? = null
) {
    override fun toString(): String {
        return "MediaItem(id=$id, title=$title, name=$name, mediaType=$mediaType)"
    }
}
