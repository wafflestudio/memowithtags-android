package com.example.memowithtags.mainMemo.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.Memo
import com.example.memowithtags.R

class MemoAdapter : RecyclerView.Adapter<MemoAdapter.MemoViewHolder>() {

    private var memoList: List<Memo> = emptyList()

    inner class MemoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val memoContent: TextView = itemView.findViewById(R.id.memoContent)
        val tagContainer: LinearLayout = itemView.findViewById(R.id.tagContainer)
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

        /*태그 표시하기
        for (tag in memo.tagIds) {
            val tagView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_tag, holder.tagContainer, false) as TextView

            tagView.text = tag.name

            val background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.tag_background)
            val wrappedDrawable = DrawableCompat.wrap(background!!).mutate()

            try {
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor(tag.colorHex))
            } catch (e: IllegalArgumentException) {
                DrawableCompat.setTint(wrappedDrawable, Color.GRAY)
            }

            tagView.background = wrappedDrawable
            holder.tagContainer.addView(tagView)
        }
         */
    }

    fun updateData(newList: List<Memo>) {
        this.memoList = newList
        notifyDataSetChanged()
    }
}
