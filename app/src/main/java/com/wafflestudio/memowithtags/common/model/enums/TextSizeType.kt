package com.wafflestudio.memowithtags.common.model.enums

enum class TextSizeType {
    SMALL,
    MEDIUM,
    BIG;

    override fun toString(): String {
        return when (this) {
            SMALL -> "SMALL"
            MEDIUM -> "MEDIUM"
            BIG -> "BIG"
        }
    }
}
