package com.example.wdww.model

import com.google.gson.annotations.SerializedName

data class AccountResponse(
    val id: Int,
    val name: String,
    val username: String,
    @SerializedName("include_adult") val includeAdult: Boolean
) 