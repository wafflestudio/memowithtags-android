package com.wafflestudio.memowithtags.mainMemo.adapters.callbacks

interface TagAdapterCallback {
    fun onTagClick(tagId: Int) {}
    fun onEditClick(tagId: Int) {}
    fun onDeleteClick(tagId: Int) {}
}
