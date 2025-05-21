package com.example.memowithtags.common.network

import com.example.memowithtags.common.model.Tag
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
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

    @HTTP(method = "DELETE", path = "api/v1/auth/withdrawal", hasBody = true)
    fun withdrawUser(
        @Header("Authorization") token: String,
        @Body request: WithdrawalRequest
    ): Call<Void>
}

interface MemoApi {
    @POST("api/v1/memo")
    fun createMemo(@Body request: CreateMemoRequest): Call<CreateMemoResponse>

    @GET("api/v1/search-memo")
    fun searchMemo(
        @Query("content") content: String?,
        @Query("tagIds") tagId: List<Int>?,
        @Query("startDate") startDate: String?,
        @Query("endDate") endDate: String?,
        @Query("page") page: Int?
    ): Call<SearchMemoResponse>
}

interface TagApi {
    @POST("/api/v1/tag")
    fun createTag(@Body request: CreateTagRequest): Call<TagResponse>

    @GET("api/v1/tag")
    fun getTags(): Call<List<Tag>>
}
