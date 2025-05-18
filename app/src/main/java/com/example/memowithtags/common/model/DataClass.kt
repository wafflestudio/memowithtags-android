package com.example.memowithtags.common.model

data class Tag(
    val id: Int,
    val name: String,
    val colorHex: String,
    val createdAt: String,
    val updatedAt: String,
)

data class Memo(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val locked: Boolean,
    val content: String,
    val tagIds: List<Int>
)

val tagColors = listOf(
    "#FF9C9C",
    "#FFBDBD",
    "#FFE3DA",
    "#FFF0B8",
    "#FFF56F",
    "#D4FDCB",
    "#DCF794",
    "#92EDA1",
    "#CCFFF7",
    "#A6F7EA",
    "#D2E8FE",
    "#B3D9FF",
    "#EEDEFE",
    "#DEBDFF",
    "#FFBDDE",
    "#FFD9EC"
)
