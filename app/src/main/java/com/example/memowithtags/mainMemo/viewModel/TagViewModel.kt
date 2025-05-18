package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.model.Tag
import com.example.memowithtags.common.network.TagResponse
import com.example.memowithtags.mainMemo.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TagViewModel @Inject constructor(
    private val repository: TagRepository
) : ViewModel() {

    private val _tagList = MutableLiveData<List<Tag>>(emptyList())
    val tagList: LiveData<List<Tag>> = _tagList

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
}
