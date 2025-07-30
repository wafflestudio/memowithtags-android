package com.example.memowithtags.common.model.response.auth

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)
