package com.example.memowithtags.mainMemo.adapters.callbacks

import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.model.MemoSource

interface MemoAdapterCallback {
    fun onSearchClick(memo: Memo) {}
    fun onEditClick(memo: Memo, tagIds: List<Int>) {}
    fun onEasyEditClick(memo: Memo, tagIds: List<Int>) {}
    fun onDeleteClick(memo: Memo, source: MemoSource) {}
}
