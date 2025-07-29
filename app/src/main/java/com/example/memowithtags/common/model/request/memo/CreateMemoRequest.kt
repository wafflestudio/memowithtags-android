package com.example.memowithtags.common.model.request.memo

data class CreateMemoRequest(
    val content: String,
    val tagIds: List<Int>,
    val locked: Boolean
)
