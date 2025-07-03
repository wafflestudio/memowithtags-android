package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.network.api.CreateMemoRequest
import com.example.memowithtags.common.network.api.UpdateMemoRequest
import com.example.memowithtags.mainMemo.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val memoRepository: MemoRepository
) : ViewModel() {

    private val _memoList = MutableLiveData<List<Memo>>(emptyList())
    val memoList: LiveData<List<Memo>> = _memoList

    private val _editingMemo = MutableLiveData<Memo?>()
    val editingMemo: LiveData<Memo?> get() = _editingMemo

    fun getMyMemos() {
        memoRepository.getMyMemos(
            content = null,
            tagIds = null,
            startDate = null,
            endDate = null,
            page = 1,
            onResult = { memos -> _memoList.postValue(memos) },
            onError = { error -> Log.e("MemoViewModel", "메모 불러오기 실패", error) }
        )
    }

    fun postMemo(content: String, tagIds: List<Int>) {
        val request = CreateMemoRequest(content, tagIds, false)

        memoRepository.postMemo(
            request = request,
            onSuccess = { memo ->
                Log.d("MemoViewModel", "메모 등록 성공: $memo")
                getMyMemos()
            },
            onError = { error ->
                Log.e("MemoViewModel", "메모 등록 실패", error)
            }
        )
    }

    fun updateMemo(memoId: Int, updatedContent: String, updatedTagIds: List<Int>, locked: Boolean) {
        val request = UpdateMemoRequest(
            updatedContent,
            updatedTagIds,
            locked
        )

        memoRepository.updateMemo(
            memoId = memoId,
            request = request,
            onSuccess = {
                clearEditing()
                getMyMemos()
            },
            onError = {
                Log.e("MemoViewModel", "메모 수정 실패", it)
            }
        )
    }

    fun getMemo(memoId: Int): Memo? {
        return _memoList.value?.find { it.id == memoId }
    }

    fun startEditing(memo: Memo) {
        _editingMemo.value = memo
    }

    fun clearEditing() {
        _editingMemo.value = null
    }
}
