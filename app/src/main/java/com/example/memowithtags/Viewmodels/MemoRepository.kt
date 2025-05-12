package com.example.memowithtags.Repository

import com.example.memowithtags.Memo
import com.example.memowithtags.Network.CreateMemoRequest
import com.example.memowithtags.Network.CreateMemoResponse
import com.example.memowithtags.Network.MemoApi
import com.example.memowithtags.Network.SearchMemoResponse
import com.example.wafflestudio_toyproject.network.ApiClient
import retrofit2.Call
import javax.inject.Inject
import retrofit2.Callback
import retrofit2.Response

class MemoRepository @Inject constructor(
    private val apiClient: ApiClient
) {
    private val memoApi = apiClient.memoApi

    fun getMyMemos(content: String?, tagIds: List<Int>?, startDate: String?, endDate: String?, page: Int?,
        onResult: (List<Memo>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        memoApi.searchMemo(content, tagIds, startDate, endDate, page)
            .enqueue(object : Callback<SearchMemoResponse> {
                override fun onResponse(
                    call: Call<SearchMemoResponse>,
                    response: Response<SearchMemoResponse>
                ) {
                    if (response.isSuccessful) {
                        onResult(response.body()?.results ?: emptyList())
                    } else {
                        onError(Throwable("서버 응답 실패: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<SearchMemoResponse>, t: Throwable) {
                    onError(t)
                }
            })
    }


    fun postMemo(
        request: CreateMemoRequest, onSuccess: (Memo) -> Unit, onError: (Throwable) -> Unit
    ) {
        memoApi.createMemo(request).enqueue(object : Callback<CreateMemoResponse> {
            override fun onResponse(
                call: Call<CreateMemoResponse>,
                response: Response<CreateMemoResponse>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val memo = Memo(
                            id = result.id,
                            content = result.content,
                            createdAt = result.createdAt,
                            updatedAt = result.updatedAt,
                            tagIds = result.tagIds,
                            locked = result.locked
                        )
                        onSuccess(memo)
                    } else {
                        onError(Throwable("응답은 성공했지만 메모가 없습니다."))
                    }
                } else {
                    onError(Throwable("서버 오류: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<CreateMemoResponse>, t: Throwable) {
                onError(t)
            }
        })
    }
}
