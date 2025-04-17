package com.example.memowithtags

data class Tag(
    val name: String,
    val colorHex: String
)

data class Memo(
    val content: String,
    val tags: List<Tag>
)

