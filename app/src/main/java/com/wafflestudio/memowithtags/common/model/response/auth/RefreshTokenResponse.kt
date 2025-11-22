package com.wafflestudio.memowithtags.common.model.response.auth

data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)
