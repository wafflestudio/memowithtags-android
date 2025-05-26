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

class SelectedTagAdapter(
    private val onTagUnselect: (Tag) -> Unit
) : RecyclerView.Adapter<SelectedTagAdapter.TagViewHolder>() {

    private val tags = mutableListOf<Tag>()

    fun submitList(newTags: List<Tag>) {
        tags.clear()
        tags.addAll(newTags)
        notifyDataSetChanged()
    }

    inner class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tagText = view.findViewById<TextView>(R.id.tagText)

        fun bind(tag: Tag) {
            tagText.text = tag.name
            (tagText.background as? GradientDrawable)?.setColor(
                try { Color.parseColor(tag.colorHex) } catch (e: IllegalArgumentException) { Color.LTGRAY }
            )

            tagText.setOnClickListener { onTagUnselect(tag) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }

    override fun getItemCount(): Int = tags.size
}
