package com.example.memowithtags.mainMemo.repository

import com.example.memowithtags.common.model.entity.Memo
import com.example.memowithtags.common.model.request.memo.CreateMemoRequest
import com.example.memowithtags.common.model.request.memo.RecommendMemoRequest
import com.example.memowithtags.common.model.request.memo.UpdateMemoRequest
import com.example.memowithtags.common.model.response.memo.CreateMemoResponse
import com.example.memowithtags.common.model.response.memo.RecommendMemoResponse
import com.example.memowithtags.common.model.response.memo.SearchMemoResponse
import com.example.memowithtags.common.network.api.MemoApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class MemoRepository @Inject constructor(
    private val memoApi: MemoApi
) {

    fun getMyMemos(
        content: String?,
        tagIds: List<Int>?,
        startDate: String?,
        endDate: String?,
        page: Int?,
        onResult: (List<Memo>, Int) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        memoApi.searchMemo(content, tagIds, startDate, endDate, page)
            .enqueue(object : Callback<SearchMemoResponse> {
                override fun onResponse(
                    call: Call<SearchMemoResponse>,
                    response: Response<SearchMemoResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val memos = body?.results ?: emptyList()
                        val totalPages = body?.totalPages ?: 1 // 기본 1페이지
                        onResult(memos, totalPages)
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
        request: CreateMemoRequest,
        onSuccess: (Memo) -> Unit,
        onError: (Throwable) -> Unit
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

    fun updateMemo(
        memoId: Int,
        request: UpdateMemoRequest,
        onSuccess: (Memo) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        memoApi.updateMemo(memoId, request)
            .enqueue(object : Callback<CreateMemoResponse> {
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

    fun deleteMemo(
        memoId: Int,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        memoApi.deleteMemo(memoId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(HttpException(response))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onError(t)
            }
        })
    }

    fun searchMemo(
        content: String,
        tagIds: List<Int>,
        startDate: String?,
        endDate: String?,
        page: Int,
        callback: (List<Memo>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        memoApi.searchMemo(content, tagIds, startDate, endDate, page)
            .enqueue(object : Callback<SearchMemoResponse> {
                override fun onResponse(
                    call: Call<SearchMemoResponse>,
                    response: Response<SearchMemoResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        callback(body?.results ?: emptyList())
                    } else {
                        onError(Exception("Response not successful: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<SearchMemoResponse>, t: Throwable) {
                    onError(t)
                }
            })
    }

    fun recommendMemos(
        content: String,
        tagIds: List<Int>,
        onSuccess: (List<Int>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val request = RecommendMemoRequest(content, tagIds)
        memoApi.recommendMemos(request).enqueue(object : Callback<RecommendMemoResponse> {
            override fun onResponse(call: Call<RecommendMemoResponse>, response: Response<RecommendMemoResponse>) {
                if (response.isSuccessful) {
                    val ids = response.body()?.memoIds ?: emptyList()
                    onSuccess(ids)
                } else {
                    onError(Exception("추천 실패: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<RecommendMemoResponse>, t: Throwable) {
                onError(t)
            }
        })
    }
}
