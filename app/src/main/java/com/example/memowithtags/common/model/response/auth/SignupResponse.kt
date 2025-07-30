package com.example.memowithtags.common.model.response.auth

data class SignupResponse(
    val accessToken: String,
    val refreshToken: String
)
