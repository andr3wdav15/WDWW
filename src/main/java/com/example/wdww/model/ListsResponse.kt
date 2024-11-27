package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class ListsResponse(
    val page: Int,
    val results: List<ListInfo>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

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
