package com.example.memowithtags.settings.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.memowithtags.settings.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainSettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _searchFilterOption = MutableLiveData<String>()
    val searchFilterOption: LiveData<String> get() = _searchFilterOption

    private val _searchSortOption = MutableLiveData<String>()
    val searchSortOption: LiveData<String> get() = _searchSortOption

    private val _textSizeOption = MutableLiveData<String>()
    val textSizeOption: LiveData<String> get() = _textSizeOption

    init {
        // initialize search filter and sort options
        if (settingsRepository.getSearchFilterOption() == null) {
            settingsRepository.setSearchFilterOption("and")
        }
        _searchFilterOption.value = settingsRepository.getSearchFilterOption()

        if (settingsRepository.getSearchSortOption() == null) {
            settingsRepository.setSearchSortOption("created")
        }
        _searchSortOption.value = settingsRepository.getSearchSortOption()

        if (settingsRepository.getTextSizeOption() == null) {
            settingsRepository.setTextSizeOption("medium")
        }
        _textSizeOption.value = settingsRepository.getTextSizeOption()
    }

    fun setSearchFilterOption(option: String) {
        settingsRepository.setSearchFilterOption(option)
        _searchFilterOption.value = option
    }

    fun setSearchSortOption(option: String) {
        settingsRepository.setSearchSortOption(option)
        _searchSortOption.value = option
    }

    fun setTextSizeOption(option: String) {
        settingsRepository.setTextSizeOption(option)
        _textSizeOption.value = option
    }
}
