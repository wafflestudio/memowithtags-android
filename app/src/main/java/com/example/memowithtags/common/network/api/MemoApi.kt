package com.example.memowithtags.common.network.api

import retrofit2.Call
import retrofit2.http.*

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

    @PUT("api/v1/memo/{id}")
    fun updateMemo(
        @Path("id") id: Int,
        @Body request: UpdateMemoRequest
    ): Call<CreateMemoResponse>
}
