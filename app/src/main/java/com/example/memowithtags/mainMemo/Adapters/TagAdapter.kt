package com.example.memowithtags.mainMemo.Adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Tag

class TagAdapter(
    private val onTagClick: (Int) -> Unit,
    private val tagResolver: (Int) -> Tag?
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private var tagList: List<Int> = emptyList()

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagName: TextView = itemView.findViewById(R.id.tagText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tagId = tagList[position]

        holder.itemView.setOnClickListener {
            onTagClick(tagId)
        }

        val tag = tagResolver(tagId) ?: return

        holder.tagName.text = tag.name

        val background = holder.tagName.background
        if (background is GradientDrawable) {
            try {
                background.setColor(Color.parseColor(tag.colorHex))
            } catch (e: IllegalArgumentException) {
                background.setColor(Color.LTGRAY)
            }
        }
    }

    override fun getItemCount(): Int = tagList.size

    fun updateData(newTags: List<Int>) {
        this.tagList = newTags
        notifyDataSetChanged()
    }
}
