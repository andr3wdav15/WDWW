package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class CreateListRequest(
    val name: String,
    val description: String,
    val language: String = "en"
)

data class CreateListResponse(
    val status_message: String,
    val success: Boolean,
    val list_id: Int
)

data class AddMediaToListRequest(
    val media_id: Int
)

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
