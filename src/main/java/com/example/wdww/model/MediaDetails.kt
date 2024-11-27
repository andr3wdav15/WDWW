package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class MediaDetails(
    val id: Int,
    val title: String?,
    val name: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    val overview: String?,
    val genres: List<Genre>,
    val credits: Credits
)

data class Genre(
    val id: Int,
    val name: String
)

data class Credits(
    val cast: List<Cast>,
    val crew: List<Crew>
)

data class Cast(
    val id: Int,
    val name: String,
    @SerializedName("profile_path") val profilePath: String?,
    val character: String
)

data class Crew(
    val id: Int,
    val name: String,
    val job: String,
    val department: String,
    val profilePath: String?
)