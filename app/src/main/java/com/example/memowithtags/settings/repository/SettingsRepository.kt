package com.example.memowithtags.settings.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun getSearchFilterOption(): String? {
        return prefs.getString("search_filter_option", null)
    }

    fun getSearchSortOption(): String? {
        return prefs.getString("search_sort_option", null)
    }

    fun setSearchFilterOption(option: String) {
        prefs.edit() { putString("search_filter_option", option) }
    }

    fun setSearchSortOption(option: String) {
        prefs.edit() { putString("search_sort_option", option) }
    }
}
