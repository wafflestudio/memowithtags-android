package com.example.memowithtags.common.network.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("api/v1/auth/register")
    fun signup(@Body signupRequest: SignupRequest): Call<SignupResponse>

    @POST("api/v1/auth/send-email")
    fun sendEmail(@Body sendEmailRequest: SendEmailRequest): Call<Unit>

    @POST("api/v1/auth/verify-email")
    fun verifyEmail(@Body verifyEmailRequest: VerifyEmailRequest): Call<Unit>

    @POST("api/v1/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/v1/auth/refresh-token")
    fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest): Call<RefreshTokenResponse>

    @POST("api/v1/auth/reset-password")
    fun changePw(@Body changePwRequest: ChangePwRequest): Call<Unit>
}
