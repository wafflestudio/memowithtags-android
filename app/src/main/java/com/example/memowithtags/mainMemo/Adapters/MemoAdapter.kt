package com.example.memowithtags.mainMemo.Adapters

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
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
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.model.MemoWithTags
import com.example.memowithtags.mainMemo.animators.ViewExpandAnimator
import com.google.android.flexbox.FlexboxLayoutManager
import java.util.Locale

class MemoAdapter(
    private val onSearchClick: ((Memo) -> Unit) ? = null,
    private val onEditClick: ((Memo, List<Int>) -> Unit) ? = null,
    private val onEasyEditClick: ((Memo, List<Int>) -> Unit) ? = null
) : ListAdapter<MemoWithTags, MemoAdapter.MemoViewHolder>(DiffCallback()) {

    private var expandedMemoIds: MutableSet<Int> = mutableSetOf()

    companion object {
        val TAG_DIFF_CALLBACK = TagAdapter.DiffCallback()
    }

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tagAdapter: TagAdapter = TagAdapter()

        init {
            itemView.findViewById<RecyclerView>(R.id.tagRecyclerView).apply {
                layoutManager = FlexboxLayoutManager(itemView.context)
                adapter = tagAdapter
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
                    onEditClick?.let { it1 -> it1(memoWithTags.memo, memoWithTags.memo.tagIds) }
                    popupWindow.dismiss()
                }

                // memo search
                popupView.findViewById<LinearLayout>(R.id.memoSearch).setOnClickListener {
                    onSearchClick?.let { it1 -> it1(memoWithTags.memo) }
                    popupWindow.dismiss()
                }

                // memo delete
                popupView.findViewById<LinearLayout>(R.id.memoDelete).setOnClickListener {
                    popupWindow.dismiss()
                }

                popupWindow.showAsDropDown(view)
                true
            }

            // buttonbar click listener
            itemView.findViewById<Button>(R.id.searchButton).setOnClickListener {
                onSearchClick?.let { it1 -> it1(memoWithTags.memo) }
            }

            itemView.findViewById<Button>(R.id.editButton).setOnClickListener {
                onEasyEditClick?.let { it1 -> it1(memoWithTags.memo, memoWithTags.memo.tagIds) }
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
                if (!Companion.TAG_DIFF_CALLBACK.areContentsTheSame(oldItem.tags[i], newItem.tags[i])) return false
            }
            return isMemoContentsSame
        }
    }
}
