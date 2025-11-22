package com.wafflestudio.memowithtags.settings.adapters

import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.memowithtags.R
import com.wafflestudio.memowithtags.common.model.tagColors

class ColorAdapter(
    private val onColorClick: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private val colors: List<String> = tagColors

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorPalette: View = itemView.findViewById(R.id.colorPalette)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_palette, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]

        val isDarkMode = holder.itemView.context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        val backgroundColor = if (isDarkMode) {
            addAlphaToColor(color).toColorInt()
        } else {
            color.toColorInt()
        }
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(backgroundColor)
        }

        holder.colorPalette.background = drawable

        holder.itemView.setOnClickListener {
            onColorClick(color)
        }
    }

    override fun getItemCount(): Int = colors.size

    private fun addAlphaToColor(hexColor: String, alpha: String = "66"): String {
        val cleanHex = hexColor.removePrefix("#")
        return "#$alpha$cleanHex"
    }
}
