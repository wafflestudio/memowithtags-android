package com.wafflestudio.memowithtags.mainMemo.adapters

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.memowithtags.R
import com.wafflestudio.memowithtags.common.model.entity.Tag
import com.wafflestudio.memowithtags.mainMemo.adapters.callbacks.TagAdapterCallback

class TagAdapter(
    private val callbacks: TagAdapterCallback,
    private val enableLongClick: Boolean = false
) : ListAdapter<Tag, TagAdapter.TagViewHolder>(DiffCallback()) {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tagName: TextView = itemView.findViewById(R.id.tagText)

        fun bind(tag: Tag) {
            // Set tag name and color
            tagName.text = tag.name

            val background = tagName.background

            if (background is GradientDrawable) {
                try {
                    // Set background color based on the current night mode
                    val nightModeFlags = itemView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                        background.setColor(addAlphaToColor(tag.colorHex).toColorInt())
                    } else if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                        background.setColor(tag.colorHex.toColorInt())
                    }
                } catch (e: IllegalArgumentException) {
                    background.setColor(Color.LTGRAY)
                }
            }

            itemView.setOnClickListener {
                callbacks.onTagClick(tag.id)
            }

            if (enableLongClick) {
                itemView.setOnLongClickListener { view ->

                    val inflater = LayoutInflater.from(view.context)
                    val popupView = inflater.inflate(R.layout.tag_context_menu, null)

                    val widthInPx = (196 * view.context.resources.displayMetrics.density + 0.5f).toInt()
                    val popupWindow = PopupWindow(
                        popupView,
                        widthInPx,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                    )
                    popupWindow.elevation = 16f

                    // tag edit
                    popupView.findViewById<LinearLayout>(R.id.tagEdit).setOnClickListener {
                        callbacks.onEditClick(tag.id)
                        popupWindow.dismiss()
                    }

                    // tag delete
                    popupView.findViewById<LinearLayout>(R.id.tagDelete).setOnClickListener {
                        callbacks.onDeleteClick(tag.id)
                        popupWindow.dismiss()
                    }

                    // show popup window
                    val location = IntArray(2)
                    view.getLocationOnScreen(location)
                    val anchorX = location[0]
                    val anchorY = location[1]
                    val displayMetrics = view.context.resources.displayMetrics
                    val screenWidth = displayMetrics.widthPixels
                    val screenHeight = displayMetrics.heightPixels

                    popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    val popupHeight = popupView.measuredHeight
                    val popupWidth = popupView.measuredWidth

                    val spaceBelow = screenHeight - (anchorY + view.height)
                    val spaceAbove = anchorY
                    val popupX = anchorX.coerceAtMost(screenWidth - popupWidth)

                    val popupY = if (spaceBelow >= popupHeight) {
                        anchorY + view.height + 8
                    } else if (spaceAbove >= popupHeight) {
                        anchorY - popupHeight
                    } else {
                        screenHeight - popupHeight
                    }

                    popupWindow.animationStyle = android.R.style.Animation_Dialog
                    popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, popupX, popupY)
                    true
                }
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

    private fun addAlphaToColor(hexColor: String, alpha: String = "66"): String {
        val cleanHex = hexColor.removePrefix("#")
        return "#$alpha$cleanHex"
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
