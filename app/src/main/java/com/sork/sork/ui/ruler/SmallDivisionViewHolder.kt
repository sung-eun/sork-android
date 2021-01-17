package com.sork.sork.ui.ruler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sork.sork.R
import com.sork.sork.databinding.ItemRulerSmallBinding

class SmallDivisionViewHolder(private val binding: ItemRulerSmallBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): SmallDivisionViewHolder {
            return SmallDivisionViewHolder(ItemRulerSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    fun bind(value: Int) {
        val paddingLeft: Int
        val paddingRight: Int

        when (value % 5) {
            1 -> {
                paddingLeft = itemView.context.resources.getDimensionPixelSize(R.dimen.padding_division_small)
                paddingRight = itemView.context.resources.getDimensionPixelSize(R.dimen.padding_division_small)
            }
            4 -> {
                paddingLeft = itemView.context.resources.getDimensionPixelSize(R.dimen.padding_division_small)
                paddingRight = itemView.context.resources.getDimensionPixelSize(R.dimen.padding_division_small)
            }
            else -> {
                paddingLeft = itemView.context.resources.getDimensionPixelSize(R.dimen.padding_division_default)
                paddingRight = itemView.context.resources.getDimensionPixelSize(R.dimen.padding_division_default)
            }
        }

        binding.root.setPadding(paddingLeft, 0, paddingRight, 0)
    }
}