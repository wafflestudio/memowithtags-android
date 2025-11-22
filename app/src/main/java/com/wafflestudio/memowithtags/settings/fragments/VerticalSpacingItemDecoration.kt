package com.wafflestudio.memowithtags.settings.fragments

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VerticalSpacingItemDecoration(private val verticalSpacing: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        // 모든 아이템에 아래쪽 간격 추가
        outRect.bottom = verticalSpacing

        // 첫 번째 row가 아닌 경우에만 위쪽 간격 추가하면 중복 간격 방지
        val position = parent.getChildAdapterPosition(view)
        val spanCount = (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1

        if (position >= spanCount) {
            outRect.top = verticalSpacing
        }
    }
}
