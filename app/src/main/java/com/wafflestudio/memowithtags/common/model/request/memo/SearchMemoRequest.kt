package com.wafflestudio.memowithtags.common.model.request.memo

data class SearchMemoRequest(
    val content: String,
    val tagIds: List<Int>,
    val startDate: String?,
    val endDate: String?,
    val page: Int
)
