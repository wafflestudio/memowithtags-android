package com.example.memowithtags.settings.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.memowithtags.common.model.TagSortType
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

    fun getTagSortOption(): TagSortType {
        return try {
            val name = prefs.getString("tag_sort_option", null)
            TagSortType.valueOf(name ?: return TagSortType.CREATED)
        } catch (e: IllegalArgumentException) {
            TagSortType.CREATED
        }
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

    fun setTagSortOption(option: TagSortType) {
        prefs.edit() { putString("tag_sort_option", option.toString()) }
    }

    fun setTagSortInMemoOption(option: Boolean) {
        prefs.edit() { putBoolean("tag_sort_in_memo_option", option) }
    }
}
