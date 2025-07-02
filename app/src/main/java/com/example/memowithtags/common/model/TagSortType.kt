package com.example.memowithtags.common.model

enum class TagSortType {
    ALPHABETIC,
    COLOR,
    CREATED;

    override fun toString(): String {
        return when (this) {
            ALPHABETIC -> "ALPHABETIC"
            COLOR -> "COLOR"
            CREATED -> "CREATED"
        }
    }
}
