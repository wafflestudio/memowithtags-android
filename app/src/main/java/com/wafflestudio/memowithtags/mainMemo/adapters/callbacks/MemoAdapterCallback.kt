package com.wafflestudio.memowithtags.mainMemo.adapters.callbacks

import com.wafflestudio.memowithtags.common.model.entity.Memo
import com.wafflestudio.memowithtags.common.model.enums.MemoSource

interface MemoAdapterCallback {
    fun onSearchClick(memo: Memo) {}
    fun onEditClick(memo: Memo, tagIds: List<Int>) {}
    fun onEasyEditClick(memo: Memo, tagIds: List<Int>) {}
    fun onDeleteClick(memo: Memo, source: MemoSource) {}
}
