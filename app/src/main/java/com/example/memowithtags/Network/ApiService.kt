package com.example.memowithtags.Network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi{
    @POST("api/v1/auth/register")
    fun signup(@Body signupRequest: SignupRequest): Call<SignupResponse>

    @POST("api/v1/auth/send-email")
    fun sendEmail(@Body sendEmailRequest: SendEmailRequest): Call<Unit>
}