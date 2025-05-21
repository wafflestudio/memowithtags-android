package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.model.Tag
import com.example.memowithtags.mainMemo.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val repository: TagRepository
) : ViewModel() {

    private val _tagList = MutableLiveData<List<Tag>>(emptyList())
    val tagList: LiveData<List<Tag>> = _tagList

    private val _selectedTags = MutableLiveData<List<Tag>>()
    val selectedTags: LiveData<List<Tag>> = _selectedTags

    fun createTag(name: String, colorHex: String) {
        repository.createTag(
            name = name,
            colorHex = colorHex,
            onSuccess = { tag ->
                val updated = _tagList.value.orEmpty().toMutableList().apply { add(tag) }
                _tagList.postValue(updated)
            },
            onError = { error ->
                Log.e("TagViewModel", "태그 생성 실패", error)
            }
        )
    }

    fun getMyTags() {
        repository.getMyTags(
            onSuccess = { tagList ->
                _tagList.value = tagList.map { it.copy(isVisible = true) }
            },
            onError = { error ->
                Log.e("TAG_FETCH", "태그 불러오기 실패: ${error.localizedMessage}")
            }
        )
    }

    fun selectTag(tag: Tag) {
        val updatedTags = _tagList.value?.map {
            if (it.id == tag.id) it.copy(isVisible = false) else it
        } ?: return
        _tagList.value = updatedTags

        val selected = _selectedTags.value.orEmpty().toMutableList().apply { add(tag) }
        _selectedTags.value = selected
    }

    fun unselectTag(tag: Tag) {
        val updatedTags = _tagList.value?.map {
            if (it.id == tag.id) it.copy(isVisible = true) else it
        } ?: return
        _tagList.value = updatedTags

        val selected = _selectedTags.value.orEmpty().toMutableList().apply { remove(tag) }
        _selectedTags.value = selected
    }

    fun clearSelectedTags() {
        val updatedTags = _tagList.value?.map { it.copy(isVisible = true) } ?: return
        _tagList.value = updatedTags
        _selectedTags.value = emptyList()
    }
}
