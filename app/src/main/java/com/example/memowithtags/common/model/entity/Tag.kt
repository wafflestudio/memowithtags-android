package com.example.memowithtags.common.model.entity

data class Tag(
    val id: Int,
    val name: String,
    val colorHex: String,
    val createdAt: String,
    val updatedAt: String,
    var isVisible: Boolean = true,
    var isFavorite: Boolean = false
)
