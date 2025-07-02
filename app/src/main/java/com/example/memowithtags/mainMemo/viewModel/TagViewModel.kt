package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.model.AlphanumComparator
import com.example.memowithtags.common.model.Tag
import com.example.memowithtags.common.model.TagSortType
import com.example.memowithtags.common.model.tagColors
import com.example.memowithtags.mainMemo.repository.TagRepository
import com.example.memowithtags.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.Collator
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _tagList = MutableLiveData<List<Tag>>(emptyList())
    val tagList: LiveData<List<Tag>> = _tagList

    private val _selectedTagIds = MutableLiveData<List<Int>>()
    val selectedTagIds: LiveData<List<Int>> = _selectedTagIds

    private var initialized = false

    // 이름 정렬을 위한 collator
    private val collator = AlphanumComparator()

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

        val selected = _selectedTagIds.value.orEmpty().toMutableList().apply { add(tagId) }
        _selectedTagIds.value = selected
        sortTag()
    }

    fun unselectTag(tagId: Int) {
        val updatedTags = _tagList.value?.map {
            if (it.id == tagId) it.copy(isVisible = true) else it
        } ?: return
        _tagList.value = updatedTags

        val selected = _selectedTagIds.value.orEmpty().toMutableList().apply { remove(tagId) }
        _selectedTagIds.value = selected
        sortTag()
    }

    fun clearSelectedTags() {
        val updatedTags = _tagList.value?.map { it.copy(isVisible = true) } ?: return
        _tagList.value = updatedTags
        _selectedTagIds.value = emptyList()
    }

    fun setSelectedTags(tagIds: List<Int>) {
        _selectedTagIds.value = tagIds

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
                _tagList.value = currentList.sortedWith(compareBy(collator) { it.name })
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
        _selectedTagIds.value = sortTagIds(_selectedTagIds.value.orEmpty())
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
