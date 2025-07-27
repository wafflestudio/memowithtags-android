package com.example.memowithtags.settings.adapters

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.Tag

class TagAdapter(
    private val tags: List<Tag>,
    private val onTagClick: (Tag) -> Unit
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    private var tagList: List<Tag> = tags

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagName: TextView = itemView.findViewById(R.id.settingsTagName)
        val tagColor: LinearLayout = itemView.findViewById(R.id.settingsTag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_settings_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tagList[position]

        holder.tagName.text = tag.name

        holder.itemView.setOnClickListener {
            onTagClick(tag)
        }

        val background = holder.tagColor.background
        if (background is GradientDrawable) {
            try {
                // Set background color based on the current night mode
                val nightModeFlags = holder.itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    background.setColor(addAlphaToColor(tag.colorHex).toColorInt())
                } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                    background.setColor(tag.colorHex.toColorInt())
                }
            } catch (e: IllegalArgumentException) {
                background.setColor(Color.LTGRAY)
            }
        }
    }

    override fun getItemCount(): Int = tagList.size

    private fun addAlphaToColor(hexColor: String, alpha: String = "66"): String {
        val cleanHex = hexColor.removePrefix("#")
        return "#$alpha$cleanHex"
    }

    fun updateData(newTags: List<Tag>) {
        this.tagList = newTags
        notifyDataSetChanged()
    }
}
