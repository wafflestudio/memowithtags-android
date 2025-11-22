package com.wafflestudio.memowithtags.common.model.enums

enum class SearchFilterType {
    AND,
    OR;

    override fun toString(): String {
        return when (this) {
            AND -> "AND"
            OR -> "OR"
        }
    }
}
