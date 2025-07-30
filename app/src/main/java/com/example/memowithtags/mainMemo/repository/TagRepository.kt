package com.example.memowithtags.mainMemo.repository

import com.example.memowithtags.common.model.entity.Tag
import com.example.memowithtags.common.model.request.tag.CreateTagRequest
import com.example.memowithtags.common.model.request.tag.UpdateTagRequest
import com.example.memowithtags.common.model.response.tag.TagResponse
import com.example.memowithtags.common.network.api.TagApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val tagApi: TagApi
) {

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
                            createdAt = result.createdAt,
                            updatedAt = result.updatedAt,
                            isVisible = true
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

    fun deleteTag(
        tagId: Int,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        tagApi.deleteTag(tagId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(Throwable("서버 응답 실패: ${response.code()}"))
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
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

    fun updateTag(
        id: Int,
        name: String,
        colorHex: String,
        onSuccess: (Tag, Response<TagResponse>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        tagApi.updateTag(id, UpdateTagRequest(name, colorHex)).enqueue(object : Callback<TagResponse> {
            override fun onResponse(call: Call<TagResponse>, response: Response<TagResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val tag = Tag(
                            id = result.id,
                            name = result.name,
                            colorHex = result.colorHex,
                            createdAt = result.createdAt,
                            updatedAt = result.updatedAt
                        )
                        onSuccess(tag, response)
                    } else {
                        onError(Throwable("응답은 성공했지만 태그가 없습니다."))
                    }
                } else {
                    onError(Throwable("서버 응답 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<TagResponse>, t: Throwable) {
                onError(t)
            }
        })
    }
}
