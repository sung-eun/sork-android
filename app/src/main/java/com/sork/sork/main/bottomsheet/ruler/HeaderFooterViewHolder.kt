package com.sork.sork.main.bottomsheet.ruler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sork.sork.R
import com.sork.sork.databinding.ItemRulerHeaderFooterBinding

class HeaderFooterViewHolder(binding: ItemRulerHeaderFooterBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): HeaderFooterViewHolder {
            return HeaderFooterViewHolder(ItemRulerHeaderFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    init {
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val largeLabelWidth = itemView.context.resources.getDimension(R.dimen.width_ruler_large_division)
        val itemWidth = (screenWidth / 2) - (largeLabelWidth / 2)
        itemView.layoutParams.width = itemWidth.toInt()
    }
}