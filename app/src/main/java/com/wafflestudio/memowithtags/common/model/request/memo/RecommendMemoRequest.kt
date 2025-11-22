package com.wafflestudio.memowithtags.common.model.request.memo

data class RecommendMemoRequest(
    val content: String,
    val tagIds: List<Int>
)
