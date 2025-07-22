package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.network.api.CreateMemoRequest
import com.example.memowithtags.common.network.api.UpdateMemoRequest
import com.example.memowithtags.mainMemo.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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

    // search 페이지용
    private val _query = MutableStateFlow("")

    private var searchCurrentPage = 1
    private var searchIsLastPage = false
    private var searchIsLoading = false

    private val searchMemoIdList = mutableListOf<Int>()

    private val _memoSearchResult = MutableLiveData<List<Int>>()
    val memoSearchResult: LiveData<List<Int>> = _memoSearchResult

    private val _tagSearchResult = MutableLiveData<List<Int>>()
    val tagSearchResult: LiveData<List<Int>> = _tagSearchResult

    private val _selectedSearchTagIds = MutableLiveData<List<Int>>(emptyList())
    val selectedSearchTagIds: LiveData<List<Int>> = _selectedSearchTagIds

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    resetSearchState()
                    performSearch(query)
                }
        }
    }

    // fragment 참고용
    private val _shouldScrollToTop = MutableLiveData<Boolean>(false)
    val shouldScrollToTop: LiveData<Boolean> get() = _shouldScrollToTop
    var isinitialPaging = false

    private var lastDeletedMemoId: Int? = null

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

                // 중복 메모 방지
                val existingIds = currentList.map { it.id }.toSet()
                val newMemos = memos.filter { it.id !in existingIds }
                _memoList.postValue(currentList + newMemos)

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
    }

    fun postMemo(content: String, tagIds: List<Int>) {
        val request = CreateMemoRequest(content, tagIds, false)

        memoRepository.postMemo(
            request = request,
            onSuccess = { memo ->
                Log.d("MemoViewModel", "메모 등록 성공: $memo")
                addMemoToList(memo)
                triggerScrollToTop()
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
                val currentList = _memoList.value.orEmpty().toMutableList()
                val index = currentList.indexOfFirst { it.id == memoId }
                if (index != -1) {
                    val oldMemo = currentList[index]
                    val updatedMemo = oldMemo.copy(
                        content = updatedContent,
                        tagIds = updatedTagIds,
                        locked = locked
                    )
                    currentList[index] = updatedMemo
                    _memoList.value = currentList
                }

                clearEditing()
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
        lastDeletedMemoId = memoId

        memoRepository.deleteMemo(
            memoId,
            onSuccess = {
                removeMemoFromListAndFill(memoId)
            },
            onError = {
                Log.e("MemoViewModel", "메모 삭제 실패", it)
            }
        )
    }

    fun deleteTagFromMemo(tagId: Int) {
        val updatedMemoList = _memoList.value?.map { memo ->
            val updatedTagIds = memo.tagIds.filter { it != tagId }
            memo.copy(tagIds = updatedTagIds)
        } ?: emptyList()
        _memoList.postValue(updatedMemoList)
    }

    fun addMemoToList(memo: Memo) {
        val updatedList = _memoList.value?.toMutableList() ?: mutableListOf()
        updatedList.add(0, memo)
        _memoList.value = updatedList
    }

    fun removeMemoFromListAndFill(memoId: Int) {
        val oldList = _memoList.value.orEmpty()

        val index = oldList.indexOfFirst { it.id == memoId }
        if (index == -1) return // 없는 메모

        val page = index / PAGE_SIZE + 1

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
                    if (newItem.id == lastDeletedMemoId) {
                        return@let
                    }

                    val current = _memoList.value.orEmpty().toMutableList()
                    if (current.none { it.id == newItem.id }) {
                        val insertIndex = (page * PAGE_SIZE - 1).coerceAtMost(current.size)
                        current.add(insertIndex, newItem)
                        Log.d("MemoViewModel", "fetchOneItemAtEndOfPage: $newItem")
                        _memoList.postValue(current)
                    }
                }
            },
            onError = {
            }
        )
    }

    private fun performSearch(query: String) {
        if (searchIsLoading || searchIsLastPage) {
            return
        }

        if (query.isBlank() && _selectedSearchTagIds.value.isNullOrEmpty()) {
            _memoSearchResult.value = emptyList()
            return
        }

        searchIsLoading = true

        memoRepository.searchMemo(
            content = query,
            tagIds = _selectedSearchTagIds.value ?: emptyList(),
            startDate = null,
            endDate = null,
            page = searchCurrentPage,
            callback = { result ->
                searchIsLoading = false
                if (result.isEmpty()) {
                    searchIsLastPage = true
                } else {
                    val ids = result.map { it.id }
                    searchMemoIdList.addAll(ids)
                    _memoSearchResult.postValue(searchMemoIdList.toList())
                    searchCurrentPage++
                }
            },
            onError = {
                searchIsLoading = false
            }
        )
    }

    fun addSelectedTagId(tagId: Int) {
        val updated = _selectedSearchTagIds.value.orEmpty().toMutableList().apply {
            if (!contains(tagId)) add(tagId)
        }
        _selectedSearchTagIds.value = updated
        resetSearchState()
        performSearch(_query.value)
    }

    fun removeSelectedTagId(tagId: Int) {
        val updated = _selectedSearchTagIds.value.orEmpty().toMutableList().apply {
            remove(tagId)
        }
        _selectedSearchTagIds.value = updated
        resetSearchState()
        performSearch(_query.value)
    }

    private fun resetSearchState() {
        searchCurrentPage = 1
        searchIsLastPage = false
        searchMemoIdList.clear()
    }

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
        resetSearchState()
    }

    fun loadNextSearchPage() {
        performSearch(_query.value)
    }

    fun triggerScrollToTop() {
        _shouldScrollToTop.value = true
    }
    fun consumeScrollToTopFlag() {
        _shouldScrollToTop.value = false
    }

    fun startEditing(memo: Memo) {
        _editingMemo.value = memo
    }

    fun clearEditing() {
        _editingMemo.value = null
    }
}
