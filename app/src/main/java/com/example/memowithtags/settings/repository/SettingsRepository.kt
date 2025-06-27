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

    fun getTextSizeOption(): String? {
        return prefs.getString("text_size_option", null)
    }

    fun getTagSortOption(): String? {
        return prefs.getString("tag_sort_option", null)
    }

    fun getTagSortInMemoOption(): Boolean {
        return prefs.getBoolean("tag_sort_in_memo_option", false)
    }

    fun setSearchFilterOption(option: String) {
        prefs.edit() { putString("search_filter_option", option) }
    }

    fun setSearchSortOption(option: String) {
        prefs.edit() { putString("search_sort_option", option) }
    }

    fun setTextSizeOption(option: String) {
        prefs.edit() { putString("text_size_option", option) }
    }

    fun setTagSortOption(option: String) {
        prefs.edit() { putString("tag_sort_option", option) }
    }

    fun setTagSortInMemoOption(option: Boolean) {
        prefs.edit() { putBoolean("tag_sort_in_memo_option", option) }
    }
}
