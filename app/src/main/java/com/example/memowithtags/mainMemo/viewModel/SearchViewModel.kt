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
    }

    private val _memoSearchResult = MutableLiveData<List<Int>>()
    val memoSearchResult: LiveData<List<Int>> = _memoSearchResult

    private val _tagSearchResult = MutableLiveData<List<Int>>()
    val tagSearchResult: LiveData<List<Int>> = _tagSearchResult

    private val _selectedTagIds = MutableLiveData<List<Int>>(emptyList())
    val selectedTagIds: LiveData<List<Int>> = _selectedTagIds

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    Log.d("SearchViewModel", "검색 쿼리: $query")
                    performSearch(query)
                }
        }
    }

    fun addSelectedTagId(tagId: Int) {
        val updated = _selectedTagIds.value.orEmpty().toMutableList().apply {
            if (!contains(tagId)) add(tagId)
        }
        _selectedTagIds.value = updated
        performSearch(_query.value)
    }

    fun removeSelectedTagId(tagId: Int) {
        val updated = _selectedTagIds.value.orEmpty().toMutableList().apply {
            remove(tagId)
        }
        _selectedTagIds.value = updated
        performSearch(_query.value)
    }

    private fun performSearch(query: String) {
        if (query.isBlank() && _selectedTagIds.value.isNullOrEmpty()) {
            _memoSearchResult.postValue(emptyList())
            return
        }

        memoRepository.searchMemo(
            content = query,
            tagIds = _selectedTagIds.value ?: emptyList(),
            startDate = null,
            endDate = null,
            page = 1,
            callback = { result ->
                _memoSearchResult.postValue(result.map { it.id })
            },
            onError = {
                _memoSearchResult.postValue(emptyList()) // 실패 시 빈 리스트 처리
            }
        )
    }
}
