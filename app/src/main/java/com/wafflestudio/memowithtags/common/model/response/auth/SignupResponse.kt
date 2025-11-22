package com.wafflestudio.memowithtags.common.model.response.auth

data class SignupResponse(
    val accessToken: String,
    val refreshToken: String
)
