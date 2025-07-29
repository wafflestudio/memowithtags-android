package com.example.memowithtags.common.model.response.memo

import com.example.memowithtags.common.model.entity.Memo

data class SearchMemoResponse(
    val page: Int,
    val totalPages: Int,
    val totalResults: Int,
    val results: List<Memo>
)
