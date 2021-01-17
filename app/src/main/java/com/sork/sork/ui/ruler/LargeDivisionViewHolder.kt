package com.sork.sork.ui.ruler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sork.sork.databinding.ItemRulerLargeBinding

class LargeDivisionViewHolder(private val binding: ItemRulerLargeBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): LargeDivisionViewHolder {
            return LargeDivisionViewHolder(ItemRulerLargeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    fun bind(value: Int) {
        binding.number.text = value.toString()
    }
}