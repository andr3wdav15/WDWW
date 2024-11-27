package com.example.wdww.network

import com.google.gson.annotations.SerializedName

data class StatusResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_message") val statusMessage: String,
    val success: Boolean
)
