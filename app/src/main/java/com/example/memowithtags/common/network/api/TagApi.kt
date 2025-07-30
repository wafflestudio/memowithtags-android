package com.example.memowithtags.common.network.api

import com.example.memowithtags.common.model.entity.Tag
import com.example.memowithtags.common.model.request.tag.CreateTagRequest
import com.example.memowithtags.common.model.request.tag.UpdateTagRequest
import com.example.memowithtags.common.model.response.tag.TagResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TagApi {
    @POST("/api/v1/tag")
    fun createTag(@Body request: CreateTagRequest): Call<TagResponse>

    @DELETE("/api/v1/tag/{tagId}")
    fun deleteTag(@Path("tagId") tagId: Int): Call<Void>

    @GET("api/v1/tag")
    fun getTags(): Call<List<Tag>>

    @PUT("api/v1/tag/{tagId}")
    fun updateTag(
        @Path("tagId") tagId: Int,
        @Body request: UpdateTagRequest
    ): Call<TagResponse>
}
