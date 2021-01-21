package com.sork.sork.main.bottomsheet.ruler

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sork.common.util.ViewUtil

class RulerAdapter(private val size: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_HEADER_FOOTER = 0
        private const val TYPE_LARGE = 1
        private const val TYPE_SMALL = 2
        private const val TYPE_EMPTY_SPACE = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER_FOOTER -> HeaderFooterViewHolder.create(parent)
            TYPE_EMPTY_SPACE -> EmptySpaceViewHolder.create(parent)
            TYPE_LARGE -> LargeDivisionViewHolder.create(parent)
            else -> SmallDivisionViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_HEADER_FOOTER || holder.itemViewType == TYPE_EMPTY_SPACE) return

        val positionExceptHeader = position - 1

        val value = positionExceptHeader / 2.toDouble()
        if (holder is LargeDivisionViewHolder) {
            holder.bind(value)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 || position == itemCount - 1) {
            return TYPE_HEADER_FOOTER
        }

        val positionExceptHeader = position - 1
        val value = positionExceptHeader / 2.toDouble()

        if (value.toString().endsWith(".5")) {
            return TYPE_EMPTY_SPACE
        } else if (value.toInt() % 5 == 0) {
            return TYPE_LARGE
        }
        return TYPE_SMALL
    }

    fun getPosition(value: Double): Int {
        return (value * 2).toInt() + 1
    }

    override fun getItemCount(): Int {
        return size * 2 + 2
    }
}

class ItemPaddingDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val lastPosition: Int? = parent.adapter?.let { it.itemCount - 1 }
        if (position == 0 ||
            (lastPosition != null && lastPosition == position)
        ) return

        val positionExceptHeader = position - 1
        val value = positionExceptHeader / 2.toDouble()

        if (positionExceptHeader == 0) {
            outRect.set(0, 0, -ViewUtil.dpToPx(9.5), 0)
        } else if (lastPosition != null && positionExceptHeader == lastPosition - 1) {
            outRect.set(-ViewUtil.dpToPx(9.5), 0, 0, 0)
        } else if (!value.toString().endsWith(".5") && value.toInt() % 5 == 0) {
            outRect.set(-ViewUtil.dpToPx(9.5), 0, -ViewUtil.dpToPx(9.5), 0)
        } else {
            outRect.set(ViewUtil.dpToPx(4.0), 0, ViewUtil.dpToPx(4.0), 0)
        }
    }
}