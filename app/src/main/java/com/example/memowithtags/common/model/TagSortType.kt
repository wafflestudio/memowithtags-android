package com.example.memowithtags.common.model

enum class TagSortType {
    ALPHABETIC,
    COLOR,
    CREATED;

    override fun toString(): String {
        return when (this) {
            ALPHABETIC -> "alphabetic"
            COLOR -> "color"
            CREATED -> "created"
        }
    }
}
