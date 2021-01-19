package com.sork.sork.main.bottomsheet.ruler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

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

        val virtualPosition = getVirtualPosition(position)
        if (holder is LargeDivisionViewHolder) {
            holder.bind(virtualPosition)
        } else if (holder is SmallDivisionViewHolder) {
            holder.bind(virtualPosition)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0 || position == itemCount - 1) {
            return TYPE_HEADER_FOOTER
        }
        val positionExceptHeader = position - 1

        if ((positionExceptHeader >= 2 && (positionExceptHeader + 5) % 7 == 0) ||
            (positionExceptHeader >= 5 && (positionExceptHeader + 2) % 7 == 0)
        ) {
            return TYPE_EMPTY_SPACE
        }

        val virtualPosition = getVirtualPosition(position)
        if (virtualPosition % 5 == 0) {
            return TYPE_LARGE
        }
        return TYPE_SMALL
    }

    fun getVirtualPosition(position: Int): Int {
        val positionExceptHeader = position - 1
        return positionExceptHeader - ((positionExceptHeader + 5) / 7) - ((positionExceptHeader + 2) / 7)
    }

    fun getPosition(virtualPosition: Int): Int {
//        return virtualPosition + 1 + ((virtualPosition) / 5) * 2
        return (virtualPosition.toDouble() * 7 / 5).roundToInt() + 1
    }

    override fun getItemCount(): Int {
        return size + 2 + (size / 5) * 2
    }
}