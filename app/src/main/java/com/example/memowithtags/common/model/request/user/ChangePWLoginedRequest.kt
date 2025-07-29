package com.example.memowithtags.common.model.request.user

data class ChangePWLoginedRequest(
    val originalPassword: String,
    val newPassword: String
)
