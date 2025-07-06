package com.example.memowithtags.mainMemo.Adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Memo
import com.example.memowithtags.common.model.Tag
import com.google.android.flexbox.FlexboxLayout
import java.util.Locale

class MemoAdapter(
    private val sortTagIds: (List<Int>) -> List<Int>,
    private val resolveTag: (Int) -> Tag?,

    private val onEditClick: ((Memo, List<Int>) -> Unit) ?= null,
    private val resolveMemo: (Int) -> Memo?

) : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    private var memoList: List<Int> = emptyList()
    private var expandedPosition: Int? = null

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoContent: TextView = itemView.findViewById(R.id.memoContent)
        val tagContainer: FlexboxLayout = itemView.findViewById(R.id.tagContainer)
        val memoCreated: TextView = itemView.findViewById(R.id.memoCreated)
        val buttonBar: LinearLayout = itemView.findViewById(R.id.buttonBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)
        return MemoViewHolder(view)
    }

    override fun getItemCount(): Int = memoList.size

    override fun onBindViewHolder(holder: MemoViewHolder, position: Int) {
        val memoId = memoList[position]
        Log.d("MemoAdapter", "memoId[$position] = $memoId")

        val memo = resolveMemo(memoId)
        if (memo == null) {
            Log.d("MemoAdapter", "resolveMemo($memoId) → null")
            return
        } else {
            Log.d("MemoAdapter", "resolveMemo($memoId) → content = ${memo.content}, tagIds = ${memo.tagIds}, createdAt = ${memo.createdAt}")
        }
        holder.memoContent.text = memo.content
        holder.memoCreated.text = formatDate(memo.createdAt)

        holder.tagContainer.removeAllViews()

        val sortedTagIds = sortTagIds(memo.tagIds)

        for (tagId in sortedTagIds) {
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

        holder.itemView.findViewById<Button>(R.id.editButton).setOnClickListener {
            onEditClick?.let { it1 -> it1(memo, sortedTagIds) }
        }
    }

    fun formatDate(isoDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())

        val date = inputFormat.parse(isoDate)
        return outputFormat.format(date)
    }

    fun updateData(newList: List<Int>) {
        this.memoList = newList
        notifyDataSetChanged()
    }
}
