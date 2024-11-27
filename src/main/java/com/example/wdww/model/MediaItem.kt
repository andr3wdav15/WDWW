package com.example.wdww.model

import com.google.gson.annotations.SerializedName

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
    override fun toString(): String {
        return "MediaItem(id=$id, title=$title, name=$name, mediaType=$mediaType)"
    }
}
