package com.example.memowithtags.mainMemo.repository

import com.example.memowithtags.common.model.Tag
import com.example.memowithtags.common.network.CreateTagRequest
import com.example.memowithtags.common.network.TagApi
import com.example.memowithtags.common.network.TagResponse
import com.example.memowithtags.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    private val tagApi = apiClient.tagApi

    fun createTag(
        name: String,
        colorHex: String,
        onSuccess: (Tag) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val request = CreateTagRequest(name, colorHex)
        tagApi.createTag(request).enqueue(object : Callback<TagResponse> {
            override fun onResponse(call: Call<TagResponse>, response: Response<TagResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val tag = Tag(
                            id = result.id,
                            name = result.name,
                            colorHex = result.colorHex,
                            createdAt= result.createdAt,
                            updatedAt = result.updatedAt
                        )
                        onSuccess(tag)
                    } else {
                        onError(Throwable("응답은 성공했지만 태그가 없습니다."))
                    }
                } else {
                    onError(Throwable("서버 오류: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TagResponse>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun getMyTags(
        onSuccess: (List<Tag>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        tagApi.getTags().enqueue(object : Callback<List<Tag>> {
            override fun onResponse(call: Call<List<Tag>>, response: Response<List<Tag>>) {
                if (response.isSuccessful) {
                    onSuccess(response.body() ?: emptyList())
                } else {
                    onError(Throwable("서버 응답 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Tag>>, t: Throwable) {
                onError(t)
            }
        })
    }
}
