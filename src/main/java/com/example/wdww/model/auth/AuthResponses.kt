package com.example.wdww.model.auth

import com.google.gson.annotations.SerializedName

data class RequestTokenResponse(
    val success: Boolean,
    @SerializedName("expires_at") val expiresAt: String,
    @SerializedName("request_token") val requestToken: String
)

data class CreateSessionRequest(
    @SerializedName("request_token") val requestToken: String
)

data class CreateSessionResponse(
    val success: Boolean,
    @SerializedName("session_id") val sessionId: String
)

data class DeleteSessionRequest(
    @SerializedName("session_id") val sessionId: String
)

data class DeleteSessionResponse(
    val success: Boolean
) 