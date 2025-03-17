package com.example.memowithtags.Network

data class SignupRequest(
    val email: String,
    val nickname: String,
    val password: String
)

data class SignupResponse(
    val accessToken: String,
    val refreshToken: String
)