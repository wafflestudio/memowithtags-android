package com.example.memowithtags.mainMemo.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Tag

class TagAdapter(
    private val onTagClick: (Int) -> Unit = {},
    private val onTagLongClick: (Int) -> Unit = {}
) : ListAdapter<Tag, TagAdapter.TagViewHolder>(DiffCallback()) {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagName: TextView = itemView.findViewById(R.id.tagText)

        fun bind(tag: Tag) {
            // Set tag name and color
            tagName.text = tag.name

            val background = tagName.background
            if (background is GradientDrawable) {
                try {
                    background.setColor(Color.parseColor(tag.colorHex))
                } catch (e: IllegalArgumentException) {
                    background.setColor(Color.LTGRAY)
                }
            }

            itemView.setOnClickListener {
                onTagClick(tag.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = getItem(position)
        return holder.bind(tag)
    }

    class DiffCallback : DiffUtil.ItemCallback<Tag>() {
        override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return (oldItem.name == newItem.name) && (oldItem.colorHex == newItem.colorHex)
        }
    }
}
