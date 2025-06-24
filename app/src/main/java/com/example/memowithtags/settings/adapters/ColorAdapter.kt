package com.example.memowithtags.settings.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.memowithtags.R
import com.example.memowithtags.common.model.tagColors

class ColorAdapter(
    private val onColorClick: (String) -> Unit
) : RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private val colors: List<String> = tagColors

    inner class ColorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorPalette: ImageView = itemView.findViewById(R.id.colorPalette)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_palette, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val color = colors[position]

        holder.colorPalette.setColorFilter(Color.parseColor(color))

        holder.itemView.setOnClickListener {
            onColorClick(color)
        }
    }

    override fun getItemCount(): Int = colors.size
}
