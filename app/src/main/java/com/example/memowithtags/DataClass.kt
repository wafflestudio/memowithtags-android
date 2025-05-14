package com.example.memowithtags

data class Tag(
    val name: String,
    val colorHex: String
)

data class Memo(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val locked: Boolean,
    val content: String,
    val tagIds: List<Int>
)
