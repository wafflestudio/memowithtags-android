package com.example.memowithtags.mainMemo.Adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.model.Tag
import com.google.android.flexbox.FlexboxLayout

class MemoAdapter(
    private val resolveTag: (Int) -> Tag?
) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    private var memoList: List<Memo> = emptyList()
    private var expandedPosition: Int? = null

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoContent: TextView = itemView.findViewById(R.id.memoContent)
        val tagContainer: FlexboxLayout = itemView.findViewById(R.id.tagContainer)
        val buttonBar: LinearLayout = itemView.findViewById(R.id.buttonBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun getItemCount(): Int = memoList.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memo = memoList[position]
        holder.memoContent.text = memo.content

        holder.tagContainer.removeAllViews()

        for (tagId in memo.tagIds) {
            val tag = resolveTag(tagId) ?: continue

            val tagView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_tag, holder.tagContainer, false) as TextView

            tagView.text = tag.name

            val background = tagView.background
            if (background is GradientDrawable) {
                try {
                    background.setColor(Color.parseColor(tag.colorHex))
                } catch (e: IllegalArgumentException) {
                    background.setColor(Color.LTGRAY)
                }
            }

            holder.tagContainer.addView(tagView)
        }

        val isExpanded = expandedPosition == position
        if (isExpanded) {
            holder.buttonBar.visibility = View.VISIBLE
            holder.buttonBar.animate().alpha(1f).translationY(0f).setDuration(300).start()
        } else {
            holder.buttonBar.animate()
                .alpha(0f).translationY(-20f).setDuration(300)
                .withEndAction { holder.buttonBar.visibility = View.GONE }
                .start()
        }

        holder.itemView.setOnClickListener {
            val previousExpanded = expandedPosition
            if (isExpanded) {
                expandedPosition = null
                notifyItemChanged(position)
            } else {
                expandedPosition = position
                notifyItemChanged(previousExpanded ?: -1)
                notifyItemChanged(position)
            }
        }
    }

    fun updateData(newList: List<Memo>) {
        this.memoList = newList
        notifyDataSetChanged()
    }
}
