package com.sork.sork.main

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sork.domain.ProductSummary

class ProductAdapter : ListAdapter<ProductSummary, RecyclerView.ViewHolder>(object :
    DiffUtil.ItemCallback<ProductSummary>() {
    override fun areItemsTheSame(oldItem: ProductSummary, newItem: ProductSummary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ProductSummary, newItem: ProductSummary): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            holder.bind(getItem(position))
        }
    }
}