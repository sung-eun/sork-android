package com.sork.sork.ui.ruler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sork.sork.databinding.ItemRulerSpaceBinding

class EmptySpaceViewHolder(binding: ItemRulerSpaceBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): EmptySpaceViewHolder {
            return EmptySpaceViewHolder(ItemRulerSpaceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }
}