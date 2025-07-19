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

    // Memo 정보를 저장하는 List
    private val _memoList = MutableLiveData<List<Memo>>(emptyList())
    val memoList: LiveData<List<Memo>> = _memoList

    // 현재 수정 중인 Memo
    private val _editingMemo = MutableLiveData<Memo?>()
    val editingMemo: LiveData<Memo?> get() = _editingMemo

    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false
    var lastLoadedItemCount = 0

    // fragment 참고용
    private val _isPaging = MutableLiveData(false)
    val isPaging: LiveData<Boolean> = _isPaging
    private var isinitialPaging = false

    val PAGE_SIZE = 15

    fun resetAndLoadFirstPage() {
        currentPage = 1
        isLastPage = false
        _memoList.value = emptyList()
        isinitialPaging = true
        loadNextPage()
    }

    fun loadNextPage() {
        if (isLoading || isLastPage) return
        if (!isinitialPaging) _isPaging.postValue(true)

        Log.d("Paging", "loadNextPage 호출됨. 현재 페이지: $currentPage")

        isLoading = true

        memoRepository.getMyMemos(
            content = null,
            tagIds = null,
            startDate = null,
            endDate = null,
            page = currentPage,
            onResult = { memos, totalPages ->
                val currentList = _memoList.value.orEmpty()
                _memoList.postValue(currentList + memos)

                lastLoadedItemCount = memos.size

                currentPage++
                isLastPage = currentPage >= totalPages + 1
                isLoading = false
            },
            onError = { error ->
                Log.e("MemoViewModel", "페이지 불러오기 실패", error)
                isLoading = false
            }
        )

        isinitialPaging = false
    }

    fun stopPaging() {
        _isPaging.postValue(false)
    }

    fun postMemo(content: String, tagIds: List<Int>) {
        val request = CreateMemoRequest(content, tagIds, false)

        memoRepository.postMemo(
            request = request,
            onSuccess = { memo ->
                Log.d("MemoViewModel", "메모 등록 성공: $memo")
                resetAndLoadFirstPage()
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
                resetAndLoadFirstPage()
            },
            onError = {
                Log.e("MemoViewModel", "메모 수정 실패", it)
            }
        )
    }

    fun getMemo(memoId: Int): Memo? {
        val memo = _memoList.value?.find { it.id == memoId }
        if (memo != null) {
            Log.d("MemoViewModel", "getMemo($memoId) → ${memo.content}")
        } else {
            Log.d("MemoViewModel", "getMemo($memoId) → null")
        }
        return memo
    }

    fun deleteMemo(memoId: Int) {
        removeMemoFromListAndFill(memoId)

        memoRepository.deleteMemo(
            memoId,
            onSuccess = {
            },
            onError = {
                Log.e("MemoViewModel", "메모 삭제 실패", it)
            }
        )
    }

    fun removeMemoFromListAndFill(memoId: Int) {
        val oldList = _memoList.value.orEmpty()
        val pageSize = 15

        val index = oldList.indexOfFirst { it.id == memoId }
        if (index == -1) return // 없는 메모

        val page = index / pageSize + 1

        val updated = oldList.toMutableList().apply {
            removeIf { it.id == memoId }
        }
        // 메모 삭제해서 아이템 수가 줄어들면 페이지네이션이 안 되는 오류 방지
        if (isLastPage && updated.size < currentPage * PAGE_SIZE) {
            isLastPage = false
        }

        _memoList.postValue(updated)
        // 아이템을 삭제하면 현재 페이지의 마지막 아이템 받아와서 채우기
        fetchOneItemAtEndOfPage(page)
    }

    private fun fetchOneItemAtEndOfPage(page: Int) {
        memoRepository.getMyMemos(
            content = null,
            tagIds = null,
            startDate = null,
            endDate = null,
            page = page,
            onResult = { result, totalPages ->
                result.getOrNull(PAGE_SIZE - 1)?.let { newItem ->
                    val current = _memoList.value.orEmpty().toMutableList()
                    if (current.none { it.id == newItem.id }) {
                        val insertIndex = (page * PAGE_SIZE - 1).coerceAtMost(current.size)
                        current.add(insertIndex, newItem)
                        _memoList.postValue(current)
                    }
                }
            },
            onError = {
            }
        )
    }

    fun startEditing(memo: Memo) {
        _editingMemo.value = memo
    }

    fun clearEditing() {
        _editingMemo.value = null
    }
}
