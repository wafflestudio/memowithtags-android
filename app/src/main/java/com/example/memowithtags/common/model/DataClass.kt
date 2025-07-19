package com.example.memowithtags.common.model

data class Tag(
    val id: Int,
    val name: String,
    val colorHex: String,
    val createdAt: String,
    val updatedAt: String,
    var isVisible: Boolean = true,
    var isFavorite: Boolean = false
)

data class Memo(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val locked: Boolean,
    val content: String,
    val tagIds: List<Int>
)

data class MemoWithTags(
    val memo: Memo,
    val tags: List<Tag>
)

val tagColors = listOf(
    "#FF9C9C",
    "#FFF56F",
    "#A5F8A1",
    "#8AEBF6",
    "#A2B4F2",
    "#E5A6F0",
    "#FA9BD1",
    "#FFBDBD",
    "#FFF0B8",
    "#DCF794",
    "#A6F7EA",
    "#B3D9FF",
    "#DEBDFF",
    "#FFBDDE",
    "#FFE3DA",
    "#FEFFB8",
    "#D4FDCB",
    "#CCFFF7",
    "#D2E8FE",
    "#EEDEFE",
    "#FFD9EC"
)
