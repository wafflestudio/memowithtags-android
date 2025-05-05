package com.example.memowithtags.Network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi{
    @POST("api/v1/auth/register")
    fun signup(@Body signupRequest: SignupRequest): Call<SignupResponse>

    @POST("api/v1/auth/send-email")
    fun sendEmail(@Body sendEmailRequest: SendEmailRequest): Call<Unit>

    @POST("api/v1/auth/verify-email")
    fun verifyEmail(@Body verifyEmailRequest: VerifyEmailRequest): Call<Unit>

    @POST("api/v1/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/v1/auth/reset-password")
    fun changePw(@Body changePwRequest: ChangePwRequest): Call<Unit>
}

interface MemoApi{
    @POST("api/v1/memo")
    fun createMemo(@Body request: CreateMemoRequest): Call<CreateMemoResponse>

    @GET("api/v1/search-memo")
    fun searchMemo(@Body request: SearchMemoRequest): Call<SearchMemoResponse>
}