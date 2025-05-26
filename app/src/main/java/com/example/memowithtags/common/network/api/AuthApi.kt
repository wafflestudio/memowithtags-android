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

    @GET("api/v1/auth/me")
    fun me(@Header("Authorization") token: String): Call<MeResponse>

    @PUT("api/v1/auth/nickname")
    fun changeNickname(@Header("Authorization") token: String, @Body request: ChangeNicknameRequest): Call<ChangeNicknameResponse>

    @PUT("api/v1/auth/password")
    fun changePWLogined(@Header("Authorization") token: String, @Body request: ChangePWLoginedRequest): Call<ChangePWLoginedResponse>

    @HTTP(method = "DELETE", path = "api/v1/auth/withdrawal", hasBody = true)
    fun withdrawUser(
        @Header("Authorization") token: String,
        @Body request: WithdrawalRequest
    ): Call<Void>
}
