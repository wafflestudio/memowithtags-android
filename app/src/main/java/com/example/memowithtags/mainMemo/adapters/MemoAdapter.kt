package com.example.memowithtags.mainMemo.adapters

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.MemoSource
import com.example.memowithtags.common.model.MemoWithTags
import com.example.memowithtags.mainMemo.adapters.callbacks.MemoAdapterCallback
import com.example.memowithtags.mainMemo.adapters.callbacks.TagAdapterCallback
import com.example.memowithtags.mainMemo.animators.ViewExpandAnimator
import com.google.android.flexbox.FlexboxLayoutManager
import java.util.Locale

class MemoAdapter(
    private val source: MemoSource,
    private val memoAdapterCallback: MemoAdapterCallback,
    private val tagAdapterCallback: TagAdapterCallback
) : ListAdapter<MemoWithTags, MemoAdapter.MemoViewHolder>(DiffCallback()) {

    private var expandedMemoIds: MutableSet<Int> = mutableSetOf()

    companion object {
        val TAG_DIFF_CALLBACK = TagAdapter.DiffCallback()
    }

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tagAdapter: TagAdapter = TagAdapter(tagAdapterCallback, true)

        init {
            itemView.findViewById<RecyclerView>(R.id.tagRecyclerView).apply {
                layoutManager = FlexboxLayoutManager(itemView.context)
                adapter = tagAdapter
                itemAnimator = null
            }
        }

        private val buttonBarContainer: FrameLayout = itemView.findViewById(R.id.buttonBarContainer)
        private val buttonBar: LinearLayout = itemView.findViewById(R.id.buttonBar)

        fun bind(memoWithTags: MemoWithTags) {
            // set memo content & created date
            val memoContent: TextView = itemView.findViewById(R.id.memoContent)
            val memoCreated: TextView = itemView.findViewById(R.id.memoCreated)

            memoContent.text = memoWithTags.memo.content
            memoCreated.text = formatDate(memoWithTags.memo.createdAt)

            tagAdapter.submitList(memoWithTags.tags)

            // expand & collapse memo
            if (memoWithTags.memo.id in expandedMemoIds) {
                ViewExpandAnimator.setExpandedState(buttonBarContainer, buttonBar)
            } else {
                ViewExpandAnimator.setCollapsedState(buttonBarContainer, buttonBar)
            }

            itemView.setOnClickListener {
                val isExpanded = memoWithTags.memo.id in expandedMemoIds

                buttonBar.animate().cancel()

                if (isExpanded) {
                    expandedMemoIds.remove(memoWithTags.memo.id)
                    ViewExpandAnimator.collapseView(buttonBarContainer, buttonBar)
                } else {
                    expandedMemoIds.add(memoWithTags.memo.id)
                    ViewExpandAnimator.expandView(buttonBarContainer, buttonBar)
                }
            }

            // show context menu
            itemView.setOnLongClickListener { view ->

                val inflater = LayoutInflater.from(view.context)
                val popupView = inflater.inflate(R.layout.memo_context_menu, null)
                val widthInPx = (196 * view.context.resources.displayMetrics.density + 0.5f).toInt()
                val popupWindow = PopupWindow(
                    popupView,
                    widthInPx,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
                )
                popupWindow.elevation = 16f

                // memo edit
                popupView.findViewById<LinearLayout>(R.id.memoEdit).setOnClickListener {
                    memoAdapterCallback.onEditClick(memoWithTags.memo, memoWithTags.memo.tagIds)
                    popupWindow.dismiss()
                }

                // memo search
                popupView.findViewById<LinearLayout>(R.id.memoSearch).setOnClickListener {
                    memoAdapterCallback.onSearchClick(memoWithTags.memo)
                    popupWindow.dismiss()
                }

                // memo delete
                popupView.findViewById<LinearLayout>(R.id.memoDelete).setOnClickListener {
                    memoAdapterCallback.onDeleteClick(memoWithTags.memo, source)
                    popupWindow.dismiss()
                }

                // show popup window
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                val anchorX = location[0]
                val anchorY = location[1]
                val displayMetrics = view.context.resources.displayMetrics
                val screenWidth = displayMetrics.widthPixels
                val screenHeight = displayMetrics.heightPixels

                popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                val popupHeight = popupView.measuredHeight
                val popupWidth = popupView.measuredWidth

                val spaceBelow = screenHeight - (anchorY + view.height)
                val spaceAbove = anchorY
                val popupX = anchorX.coerceAtMost(screenWidth - popupWidth)

                val popupY = if (spaceBelow >= popupHeight) {
                    anchorY + view.height + 12
                } else if (spaceAbove >= popupHeight) {
                    anchorY - popupHeight
                } else {
                    screenHeight - popupHeight
                }

                popupWindow.animationStyle = android.R.style.Animation_Dialog
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, popupX, popupY)
                true
            }

            // buttonbar click listener
            itemView.findViewById<Button>(R.id.searchButton).setOnClickListener {
                memoAdapterCallback.onSearchClick(memoWithTags.memo)
            }

            itemView.findViewById<Button>(R.id.editButton).setOnClickListener {
                memoAdapterCallback.onEasyEditClick(memoWithTags.memo, memoWithTags.memo.tagIds)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memoWithTags = getItem(position) ?: return
        holder.bind(memoWithTags)
    }

    fun removeItem(memoId: Int) {
        val currentList = currentList.toMutableList()
        val index = currentList.indexOfFirst { it.memo.id == memoId }

        if (index != -1) {
            currentList.removeAt(index)
            submitList(currentList)
        }
    }
    private fun formatDate(isoDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        val date = inputFormat.parse(isoDate)
        return outputFormat.format(date)
    }

    class DiffCallback : DiffUtil.ItemCallback<MemoWithTags>() {
        override fun areItemsTheSame(oldItem: MemoWithTags, newItem: MemoWithTags): Boolean {
            return oldItem.memo.id == newItem.memo.id
        }

        override fun areContentsTheSame(oldItem: MemoWithTags, newItem: MemoWithTags): Boolean {
            val isMemoContentsSame = oldItem.memo.content == newItem.memo.content

            if (oldItem.tags.size != newItem.tags.size) return false
            for (i in oldItem.tags.indices) {
                if (!TAG_DIFF_CALLBACK.areContentsTheSame(oldItem.tags[i], newItem.tags[i])) return false
            }
            return isMemoContentsSame
        }
    }
}
