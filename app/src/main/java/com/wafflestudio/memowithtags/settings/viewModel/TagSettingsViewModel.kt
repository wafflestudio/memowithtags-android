package com.wafflestudio.memowithtags.settings.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wafflestudio.memowithtags.common.model.entity.Tag
import com.wafflestudio.memowithtags.common.model.enums.TagSortType
import com.wafflestudio.memowithtags.common.model.response.tag.TagResponse
import com.wafflestudio.memowithtags.mainMemo.repository.TagRepository
import com.wafflestudio.memowithtags.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TagSettingsViewModel @Inject constructor(
    private val tagRepository: TagRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // 태그 설정창
    private val _tagList = MutableLiveData<List<Tag>>(emptyList())
    val tagList: LiveData<List<Tag>> = _tagList

    private val _tagSortOption = MutableLiveData<TagSortType>()
    val tagSortOption: LiveData<TagSortType> get() = _tagSortOption

    private val _tagSortInMemoOption = MutableLiveData<Boolean>()
    val tagSortInMemoOption: LiveData<Boolean> get() = _tagSortInMemoOption

    // 태그 수정 관련
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

    init {
        tagRepository.getMyTags(
            onSuccess = { tagList ->
                _tagList.value = tagList.map { it.copy(isVisible = true) }
            },
            onError = { error ->
                Log.e("TAG_FETCH", "태그 불러오기 실패: ${error.localizedMessage}")
            }
        )

        // initialize tag sort option
        _tagSortOption.value = settingsRepository.getTagSortOption()

        _tagSortInMemoOption.value = settingsRepository.getTagSortInMemoOption()
    }

    fun setTagSortOption(option: TagSortType) {
        settingsRepository.setTagSortOption(option)
        _tagSortOption.value = option
    }

    fun setTagSortInMemoOption(option: Boolean) {
        settingsRepository.setTagSortInMemoOption(option)
        _tagSortInMemoOption.value = option
    }

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
                    _tagList.value = _tagList.value?.map {
                        if (it.id == tag.id) tag else it
                    }
                    _editTagResult.value = Result.success(response.body()!!)
                },
                onError = { error ->
                    Log.e("TAG_UPDATE", "태그 업데이트 실패: ${error.localizedMessage}")
                }
            )
        }
    }

    fun resetEditState() {
        _selectedTag.value = null
        _tagNameInput.value = null
        _colorSelected.value = null
        _tagConfirmBtnEnabled.value = false
        _editTagResult.value = null
    }
}
