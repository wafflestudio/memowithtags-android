package com.example.memowithtags.mainMemo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.mainMemo.repository.MemoRepository
import com.example.memowithtags.mainMemo.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val memo_repository: MemoRepository,
    private val tag_repository: TagRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    private val _memoSearchResult = MutableLiveData<List<Memo>>()
    val memoSearchResult: LiveData<List<Memo>> = _memoSearchResult

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    private fun performSearch(query: String) {
        memo_repository.searchMemo(
            content = query,
            tagIds = emptyList(),
            startDate = null,
            endDate = null,
            page = 0,
            callback = { result ->
                _memoSearchResult.postValue(result)
            },
            onError = {
                _memoSearchResult.postValue(emptyList()) // 실패 시 빈 리스트 처리
            }
        )
    }
}