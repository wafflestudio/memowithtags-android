package com.example.memowithtags.common.model

enum class SearchSortType {
    CREATED,
    MODIFIED;

    override fun toString(): String {
        return when (this) {
            CREATED -> "CREATED"
            MODIFIED -> "MODIFIED"
        }
    }
}
