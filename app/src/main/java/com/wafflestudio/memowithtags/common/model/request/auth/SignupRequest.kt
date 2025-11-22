package com.wafflestudio.memowithtags.common.model.request.auth

data class SignupRequest(
    val email: String,
    val nickname: String,
    val password: String
)
