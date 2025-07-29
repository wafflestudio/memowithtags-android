package com.example.memowithtags.common.model.entity

data class Memo(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val locked: Boolean,
    val content: String,
    val tagIds: List<Int>
)
