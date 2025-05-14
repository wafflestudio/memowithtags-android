package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.network.CreateMemoRequest
import com.example.memowithtags.mainMemo.repository.MemoRepository
import com.example.wafflestudio_toyproject.network.ApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val apiClient: ApiClient,
    private val repository: MemoRepository
) : ViewModel() {
    private val memoApi = apiClient.memoApi

    private val _memoList = MutableLiveData<List<Memo>>(emptyList())
    val memoList: LiveData<List<Memo>> = _memoList

    fun getMyMemos() {
        repository.getMyMemos(
            content = null,
            tagIds = null,
            startDate = null,
            endDate = null,
            page = 1,
            onResult = { memos -> _memoList.postValue(memos) },
            onError = { error -> Log.e("MemoViewModel", "메모 불러오기 실패", error) }
        )
    }

    fun postMemo(content: String) {
        // 태그 추가하면 태그 ID 보내도록 수정할것!!
        val request = CreateMemoRequest(content, listOf(0), false)

        repository.postMemo(
            request = request,
            onSuccess = { memo ->
                getMyMemos()
            },
            onError = { error ->
                Log.e("MemoViewModel", "메모 등록 실패", error)
            }
        )
    }
}
