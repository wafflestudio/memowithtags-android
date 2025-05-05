package com.example.memowithtags.Network

import com.example.memowithtags.Memo

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



data class SignupResponse(
    val accessToken: String,
    val refreshToken: String
)

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

data class CreateMemoResponse(
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
    val results: List<Memo>
)

data class SearchMemoResponse(
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
    val results: List<Memo>
)
