package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class AddToFavoritesRequest(
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("media_id") val mediaId: Int,
    @SerializedName("favorite") val favorite: Boolean
)
