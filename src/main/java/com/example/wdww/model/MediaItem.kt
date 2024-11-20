package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class MediaItem(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("original_name") val originalName: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("media_type") val mediaType: String?,
    val adult: Boolean?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
    val popularity: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    val video: Boolean?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("origin_country") val originCountry: List<String>?
)

