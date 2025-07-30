package com.example.memowithtags.common.model.response.user

import java.util.UUID

data class ChangeNicknameResponse(
    val id: UUID,
    val userNumber: Int,
    val email: String,
    val nickname: String,
    val isSocial: Boolean,
    val createdAt: String
)
