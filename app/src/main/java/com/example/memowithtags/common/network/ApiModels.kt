package com.example.memowithtags.common.network

import com.example.memowithtags.common.model.Memo
import java.util.UUID

data class SignupRequest(
    val email: String,
    val nickname: String,
    val password: String
)

data class SendEmailRequest(
    val email: String
)

data class VerifyEmailRequest(
    val email: String,
    val verificationCode: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class ChangePwRequest(
    val email: String,
    val password: String
)

data class CreateMemoRequest(
    val content: String,
    val tagIds: List<Int>,
    val locked: Boolean
)

data class WithdrawalRequest(
    val email: String
)

data class ChangeNicknameRequest(
    val nickname: String
)

data class CreateTagRequest(
    val name: String,
    val colorHex: String
)

data class SignupResponse(
    val accessToken: String,
    val refreshToken: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

data class MeResponse(
    val id: UUID,
    val userNumber: Int,
    val email: String,
    val nickname: String,
    val isSocial: Boolean,
    val createdAt: String
)

data class ChangeNicknameResponse(
    val id: UUID,
    val userNumber: Int,
    val email: String,
    val nickname: String,
    val isSocial: Boolean,
    val createdAt: String
)

data class CreateMemoResponse(
    val id: Int,
    val content: String,
    val tagIds: List<Int>,
    val createdAt: String,
    val updatedAt: String,
    val locked: Boolean
)

data class SearchMemoResponse(
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
    val results: List<Memo>
)

data class TagResponse(
    val id: Int,
    val name: String,
    val colorHex: String,
    val createdAt: String,
    val updatedAt: String
)
