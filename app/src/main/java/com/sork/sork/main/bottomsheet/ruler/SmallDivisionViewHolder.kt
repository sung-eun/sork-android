package com.sork.sork.main.bottomsheet.ruler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sork.sork.databinding.ItemRulerSmallBinding

class SmallDivisionViewHolder(binding: ItemRulerSmallBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): SmallDivisionViewHolder {
            return SmallDivisionViewHolder(ItemRulerSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}