package com.example.memowithtags.mainMemo.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.model.entity.Tag
import com.example.memowithtags.common.model.response.tag.TagResponse
import com.example.memowithtags.mainMemo.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TagEditViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {
    private val _selectedTag = MutableLiveData<Tag?>(null)
    val selectedTag: LiveData<Tag?> get() = _selectedTag

    private val _tagNameInput = MutableLiveData<String?>(null)
    val tagNameInput: LiveData<String?> get() = _tagNameInput

    private val _colorSelected = MutableLiveData<String?>(null)
    val colorSelected: LiveData<String?> get() = _colorSelected

    private val _tagConfirmBtnEnabled = MutableLiveData<Boolean>(false)
    val tagConfirmBtnEnabled: LiveData<Boolean> get() = _tagConfirmBtnEnabled

    private val _editTagResult = MutableLiveData<Result<TagResponse>?>(null)
    val editTagResult: LiveData<Result<TagResponse>?> get() = _editTagResult

    fun selectTag(tag: Tag) {
        _selectedTag.value = tag
        _tagNameInput.value = tag.name
        _colorSelected.value = tag.colorHex
    }

    fun selectColor(color: String) {
        _colorSelected.value = color
        _tagConfirmBtnEnabled.value = (_tagNameInput.value!!.isNotEmpty()) && (_tagNameInput.value!!.length <= 10)
    }

    fun onTextChanged(text: String) {
        _tagNameInput.value = text
        _tagConfirmBtnEnabled.value = (text.isNotEmpty()) && (text.length <= 10)
    }

    fun editTag() {
        _selectedTag.value?.let {
            tagRepository.updateTag(
                it.id,
                _tagNameInput.value!!,
                _colorSelected.value!!,
                onSuccess = { tag, response ->
                    _editTagResult.value = Result.success(response.body()!!)
                },
                onError = { error ->
                    Log.e("TAG_UPDATE", "태그 업데이트 실패: ${error.localizedMessage}")
                }
            )
        }
    }
}
