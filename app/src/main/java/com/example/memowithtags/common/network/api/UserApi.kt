package com.example.memowithtags.common.network.api

import com.example.memowithtags.common.model.request.user.ChangeNicknameRequest
import com.example.memowithtags.common.model.request.user.ChangePWLoginedRequest
import com.example.memowithtags.common.model.request.user.WithdrawalRequest
import com.example.memowithtags.common.model.response.user.ChangeNicknameResponse
import com.example.memowithtags.common.model.response.user.ChangePWLoginedResponse
import com.example.memowithtags.common.model.response.user.MeResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
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
