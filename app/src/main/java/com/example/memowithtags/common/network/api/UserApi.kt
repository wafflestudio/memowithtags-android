package com.example.memowithtags.common.network.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.PUT

interface UserApi {
    @GET("api/v1/auth/me")
    fun me(): Call<MeResponse>

    @PUT("api/v1/auth/nickname")
    fun changeNickname(@Body request: ChangeNicknameRequest): Call<ChangeNicknameResponse>

    @PUT("api/v1/auth/password")
    fun changePWLogined(@Body request: ChangePWLoginedRequest): Call<ChangePWLoginedResponse>

    @HTTP(method = "DELETE", path = "api/v1/auth/withdrawal", hasBody = true)
    fun withdrawUser(
        @Body request: WithdrawalRequest
    ): Call<Void>
}