package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class TrendingResponse(
    val page: Int,
    val results: List<MediaItem>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)