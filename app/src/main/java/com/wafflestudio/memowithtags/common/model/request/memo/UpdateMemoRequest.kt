package com.wafflestudio.memowithtags.common.model.request.memo

data class UpdateMemoRequest(
    val content: String,
    val tagIds: List<Int>,
    val locked: Boolean
)
