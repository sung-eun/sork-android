package com.sork.sork.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.sork.domain.entity.ProductSummary
import com.sork.sork.R
import com.sork.sork.databinding.ItemMainProductBinding

class ProductViewHolder(private val binding: ItemMainProductBinding) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun create(parent: ViewGroup): ProductViewHolder {
            return ProductViewHolder(ItemMainProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    private val radius: Int by lazy {
        itemView.context.resources.getDimensionPixelSize(R.dimen.main_thumbnail_radius)
    }

    fun bind(item: ProductSummary) {
        Glide.with(itemView.context)
            .load(item.imageUrl)
            .transform(CenterCrop(), RoundedCorners(radius))
            .into(binding.thumbnail)

        binding.brandNameText.text = item.brandName
        binding.descriptionText.text = item.description
        binding.priceText.text = item.price.toString()
    }

}