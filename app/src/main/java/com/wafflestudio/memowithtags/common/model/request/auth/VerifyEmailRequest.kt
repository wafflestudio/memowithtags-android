package com.wafflestudio.memowithtags.common.model.request.auth

data class VerifyEmailRequest(
    val email: String,
    val verificationCode: String
)
