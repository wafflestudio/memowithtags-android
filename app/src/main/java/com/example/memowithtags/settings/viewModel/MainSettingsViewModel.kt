package com.example.memowithtags.settings.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.common.model.SearchFilterType
import com.example.memowithtags.common.model.SearchSortType
import com.example.memowithtags.common.model.TextSizeType
import com.example.memowithtags.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _searchFilterOption = MutableLiveData<SearchFilterType>()
    val searchFilterOption: LiveData<SearchFilterType> get() = _searchFilterOption

    private val _searchSortOption = MutableLiveData<SearchSortType>()
    val searchSortOption: LiveData<SearchSortType> get() = _searchSortOption

    private val _textSizeOption = MutableLiveData<TextSizeType>()
    val textSizeOption: LiveData<TextSizeType> get() = _textSizeOption

    init {
        // initialize search filter and sort options
        _searchFilterOption.value = settingsRepository.getSearchFilterOption()
        _searchSortOption.value = settingsRepository.getSearchSortOption()
        _textSizeOption.value = settingsRepository.getTextSizeOption()
    }

    fun setSearchFilterOption(option: SearchFilterType) {
        settingsRepository.setSearchFilterOption(option)
        _searchFilterOption.value = option
    }

    fun setSearchSortOption(option: SearchSortType) {
        settingsRepository.setSearchSortOption(option)
        _searchSortOption.value = option
    }

    fun setTextSizeOption(option: TextSizeType) {
        settingsRepository.setTextSizeOption(option)
        _textSizeOption.value = option
    }
}
