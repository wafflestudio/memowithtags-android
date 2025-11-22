package com.wafflestudio.memowithtags.common.model.response.memo

data class CreateMemoResponse(
    val id: Int,
    val content: String,
    val tagIds: List<Int>,
    val createdAt: String,
    val updatedAt: String,
    val locked: Boolean
)
