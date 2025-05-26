package com.example.memowithtags.common.network.api

import com.example.memowithtags.common.model.Tag
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TagApi {
    @POST("/api/v1/tag")
    fun createTag(@Body request: CreateTagRequest): Call<TagResponse>

    @GET("api/v1/tag")
    fun getTags(): Call<List<Tag>>
}
