package com.example.memowithtags.Viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memowithtags.Memo
import com.example.memowithtags.Network.CreateMemoRequest
import com.example.memowithtags.Network.CreateMemoResponse
import com.example.memowithtags.Network.MemoApi
import com.example.memowithtags.Network.SearchMemoRequest
import com.example.memowithtags.Repository.MemoRepository
import com.example.wafflestudio_toyproject.network.ApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import retrofit2.Call
import retrofit2.Callback

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val apiClient: ApiClient,
    private val repository: MemoRepository
) : ViewModel() {
    private val memoApi = apiClient.memoApi

    private val _memoList = MutableLiveData<List<Memo>>(emptyList())
    val memoList: LiveData<List<Memo>> = _memoList

    fun getMyMemos() {
        val request = SearchMemoRequest("",null,null,null,null)
        repository.getMyMemos(
            request = request,
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
                val updatedList = _memoList.value.orEmpty().toMutableList().apply {
                    add(0, memo)
                }
                _memoList.postValue(updatedList)
            },
            onError = { error ->
                Log.e("MemoViewModel", "메모 등록 실패", error)
            }
        )
    }
}
