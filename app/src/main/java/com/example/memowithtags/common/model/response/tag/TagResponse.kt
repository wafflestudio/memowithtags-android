package com.example.memowithtags.common.model.response.tag

data class TagResponse(
    val id: Int,
    val name: String,
    val colorHex: String,
    val createdAt: String,
    val updatedAt: String
)
