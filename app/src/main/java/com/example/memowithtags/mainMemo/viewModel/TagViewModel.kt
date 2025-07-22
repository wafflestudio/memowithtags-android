package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memowithtags.common.model.AlphanumComparator
import com.example.memowithtags.common.model.Tag
import com.example.memowithtags.common.model.TagSortType
import com.example.memowithtags.common.model.tagColors
import com.example.memowithtags.mainMemo.repository.TagRepository
import com.example.memowithtags.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private var initialized = false

    // Tag 정보를 저장하는 List
    private val _tagList = MutableLiveData<List<Tag>>(emptyList())
    val tagList: LiveData<List<Tag>> = _tagList

    // 선택된 Tag의 id를 저장하는 List
    private val _selectedTagIds = MutableLiveData<LinkedHashSet<Int>>()
    val selectedTagIds: LiveData<LinkedHashSet<Int>> = _selectedTagIds

    // 이름 정렬을 위한 collator
    private val collator = AlphanumComparator()

    // 검색 중인지 여부를 저장하는 변수
    private val _isSearching = MutableStateFlow(false)

    // 검색된 Tag의 id를 저장하는 List
    private val _searchTagIds = MutableLiveData<List<Int>>()
    val searchTagIds: LiveData<List<Int>> = _searchTagIds

    // 검색어를 저장하는 변수
    private val _query = MutableStateFlow("")

    // 검색된 Tag의 id를 저장하는 List
    private val _tagSearchResult = MutableLiveData<List<Int>>()
    val tagSearchResult: LiveData<List<Int>> = _tagSearchResult

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }

    init {
        viewModelScope.launch {
            _query
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performTagSearch(query)
                }
        }
    }

    fun setIsSearching(isSearching: Boolean) {
        _isSearching.value = isSearching
    }

    private fun performTagSearch(query: String) {
        if (!_isSearching.value) return

        if (query.isBlank()) {
            _tagSearchResult.postValue(emptyList())
            return
        }

        val result = _tagList.value
            ?.filter { it.name.contains(query, ignoreCase = true) }
            ?.map { it.id }
            ?: emptyList()

        _tagSearchResult.postValue(result)
    }

    fun clearSearch() {
        _tagSearchResult.postValue(emptyList())
        _query.value = ""
        _isSearching.value = false
    }

    fun createTag(name: String, colorHex: String) {
        tagRepository.createTag(
            name = name,
            colorHex = colorHex,
            onSuccess = { tag ->
                val updated = _tagList.value.orEmpty().toMutableList().apply { add(tag) }
                _tagList.postValue(updated)
                sortTag()
            },
            onError = { error ->
                Log.e("TagViewModel", "태그 생성 실패", error)
            }
        )
    }

    fun deleteTag(tagId: Int, onComplete: (Boolean) -> Unit) {
        tagRepository.deleteTag(
            tagId = tagId,
            onSuccess = {
                val updated = _tagList.value.orEmpty().toMutableList().apply {
                    removeIf { it.id == tagId }
                }
                _tagList.postValue(updated)
                val selected = LinkedHashSet(_selectedTagIds.value.orEmpty()).apply {
                    removeIf { it == tagId }
                }
                _selectedTagIds.postValue(selected)
                val search = _searchTagIds.value.orEmpty().toMutableList().apply {
                    removeIf { it == tagId }
                }
                _searchTagIds.postValue(search)
                sortTag()
                onComplete(true)
            },
            onError = { error ->
                Log.e("TagViewModel", "태그 삭제 실패", error)
                onComplete(false)
            }
        )
    }

    // tagList를 initialize하는 함수
    fun getMyTags() {
        if (initialized) return
        initialized = true

        tagRepository.getMyTags(
            onSuccess = { tagList ->
                _tagList.value = tagList.map { it.copy(isVisible = true) }
                sortTag()
            },
            onError = { error ->
                Log.e("TAG_FETCH", "태그 불러오기 실패: ${error.localizedMessage}")
            }
        )
    }

    // tagList에 속한 tag를 update하는 함수
    fun reloadTags() {
        tagRepository.getMyTags(
            onSuccess = { tagList ->
                _tagList.value = tagList.map {
                    if (_selectedTagIds.value?.contains(it.id) == true) {
                        it.copy(isVisible = false)
                    } else {
                        it.copy(isVisible = true)
                    }
                }
                sortTag()
            },
            onError = { error ->
                Log.e("TAG_FETCH", "태그 불러오기 실패: ${error.localizedMessage}")
            }
        )
    }

    // tagId로 tag를 불러오는 함수
    fun getTag(id: Int): Tag? {
        return _tagList.value?.find { it.id == id }
    }

    fun selectTag(tagId: Int) {
        val updatedTags = _tagList.value?.map {
            if (it.id == tagId) it.copy(isVisible = false) else it
        } ?: return
        _tagList.value = updatedTags

        val selected = LinkedHashSet(_selectedTagIds.value.orEmpty()).apply {
            add(tagId)
        }
        _selectedTagIds.value = selected
        sortTag()
    }

    fun unselectTag(tagId: Int) {
        val updatedTags = _tagList.value?.map {
            if (it.id == tagId) it.copy(isVisible = true) else it
        } ?: return
        _tagList.value = updatedTags

        val selected = LinkedHashSet(_selectedTagIds.value.orEmpty()).apply {
            remove(tagId)
        }
        _selectedTagIds.value = selected
        sortTag()
    }

    fun clearSelectedTags() {
        val updatedTags = _tagList.value?.map { it.copy(isVisible = true) } ?: return
        _tagList.value = updatedTags
        _selectedTagIds.value = LinkedHashSet()
    }

    fun setSelectedTags(tagIds: List<Int>) {
        _selectedTagIds.value = LinkedHashSet(tagIds)

        // visibility 수정하기
        val currentList = _tagList.value ?: return
        val updated = currentList.map { tag ->
            if (tagIds.any { it == tag.id }) {
                tag.copy(isVisible = false)
            } else {
                tag.copy(isVisible = true)
            }
        }
        _tagList.value = updated
    }

    // tagList와 selectedTags를 정렬하는 함수
    private fun sortTag() {
        val currentList = _tagList.value ?: return
        when (settingsRepository.getTagSortOption()) {
            TagSortType.ALPHABETIC -> {
                _tagList.value = currentList.sortedBy { it.name }
            }
            TagSortType.COLOR -> {
                _tagList.value = currentList.sortedBy { tag ->
                    tagColors.indexOf(tag.colorHex).let { if (it != -1) it else Int.MAX_VALUE }
                }
            }
            TagSortType.CREATED -> {
                _tagList.value = currentList.sortedByDescending { it.createdAt }
            }
        }
        _selectedTagIds.value = LinkedHashSet(sortTagIds(_selectedTagIds.value.orEmpty().toList()))
    }

    // memo의 tagIds를 정렬하는 함수
    fun sortTagIds(tagIds: List<Int>): List<Int> {
        if (settingsRepository.getTagSortInMemoOption()) {
            return when (settingsRepository.getTagSortOption()) {
                TagSortType.ALPHABETIC -> {
                    tagIds.sortedWith(compareBy(collator) { _tagList.value?.find { tag -> tag.id == it }?.name.toString() })
                }
                TagSortType.COLOR -> {
                    tagIds.sortedBy { tagId ->
                        val color = _tagList.value?.find { it.id == tagId }?.colorHex
                        tagColors.indexOf(color).let { if (it != -1) it else Int.MAX_VALUE }
                    }
                }
                TagSortType.CREATED -> {
                    tagIds.sortedBy { _tagList.value?.find { tag -> tag.id == it }?.createdAt }
                }
            }
        } else {
            return tagIds
        }
    }
}
