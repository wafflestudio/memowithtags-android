package com.wafflestudio.memowithtags.settings.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.wafflestudio.memowithtags.common.model.enums.SearchFilterType
import com.wafflestudio.memowithtags.common.model.enums.SearchSortType
import com.wafflestudio.memowithtags.common.model.enums.TagSortType
import com.wafflestudio.memowithtags.common.model.enums.TextSizeType
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val prefs: SharedPreferences
) {
    fun getSearchFilterOption(): SearchFilterType {
        return try {
            val name = prefs.getString("search_filter_option", null)
            SearchFilterType.valueOf(name ?: return SearchFilterType.AND)
        } catch (e: IllegalArgumentException) {
            SearchFilterType.AND
        }
    }

    fun getSearchSortOption(): SearchSortType {
        return try {
            val name = prefs.getString("search_sort_option", null)
            SearchSortType.valueOf(name ?: return SearchSortType.CREATED)
        } catch (e: IllegalArgumentException) {
            SearchSortType.CREATED
        }
    }

    fun getTextSizeOption(): TextSizeType {
        return try {
            val name = prefs.getString("text_size_option", null)
            TextSizeType.valueOf(name ?: return TextSizeType.MEDIUM)
        } catch (e: IllegalArgumentException) {
            TextSizeType.MEDIUM
        }
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

    fun setSearchFilterOption(option: SearchFilterType) {
        prefs.edit() { putString("search_filter_option", option.toString()) }
    }

    fun setSearchSortOption(option: SearchSortType) {
        prefs.edit() { putString("search_sort_option", option.toString()) }
    }

    fun setTextSizeOption(option: TextSizeType) {
        prefs.edit() { putString("text_size_option", option.toString()) }
    }

    fun setTagSortOption(option: TagSortType) {
        prefs.edit() { putString("tag_sort_option", option.toString()) }
    }

    fun setTagSortInMemoOption(option: Boolean) {
        prefs.edit() { putBoolean("tag_sort_in_memo_option", option) }
    }
}
