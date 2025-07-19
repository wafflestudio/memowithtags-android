package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memowithtags.mainMemo.repository.MemoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val memoRepository: MemoRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    fun updateQuery(newQuery: String) {
        _query.value = newQuery
        resetSearchState()
    }

    private val _memoSearchResult = MutableLiveData<List<Int>>()
    val memoSearchResult: LiveData<List<Int>> = _memoSearchResult

    private val _tagSearchResult = MutableLiveData<List<Int>>()
    val tagSearchResult: LiveData<List<Int>> = _tagSearchResult

    private val _selectedTagIds = MutableLiveData<List<Int>>(emptyList())
    val selectedTagIds: LiveData<List<Int>> = _selectedTagIds

    private val memoIdList = mutableListOf<Int>()
    private var currentPage = 1
    private var isLastPage = false
    private var isLoading = false

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    Log.d("SearchViewModel", "검색 쿼리: $query")
                    resetSearchState()
                    performSearch(query)
                }
        }
    }

    fun addSelectedTagId(tagId: Int) {
        val updated = _selectedTagIds.value.orEmpty().toMutableList().apply {
            if (!contains(tagId)) add(tagId)
        }
        _selectedTagIds.value = updated
        resetSearchState()
        performSearch(_query.value)
    }

    fun removeSelectedTagId(tagId: Int) {
        val updated = _selectedTagIds.value.orEmpty().toMutableList().apply {
            remove(tagId)
        }
        _selectedTagIds.value = updated
        resetSearchState()
        performSearch(_query.value)
    }

    private fun resetSearchState() {
        currentPage = 1
        isLastPage = false
        memoIdList.clear()
    }

    private fun performSearch(query: String) {
        if ((query.isBlank() && _selectedTagIds.value.isNullOrEmpty()) || isLoading || isLastPage) {
            return
        }

        isLoading = true

        memoRepository.searchMemo(
            content = query,
            tagIds = _selectedTagIds.value ?: emptyList(),
            startDate = null,
            endDate = null,
            page = currentPage,
            callback = { result ->
                isLoading = false
                if (result.isEmpty()) {
                    isLastPage = true
                } else {
                    val ids = result.map { it.id }
                    memoIdList.addAll(ids)
                    _memoSearchResult.postValue(memoIdList.toList())
                    currentPage++
                }
            },
            onError = {
                isLoading = false
            }
        )
    }

    fun loadNextPage() {
        performSearch(_query.value)
    }
}
