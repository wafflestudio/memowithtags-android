package com.wafflestudio.memowithtags.common.model.response.auth

data class SocialLoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean
)
