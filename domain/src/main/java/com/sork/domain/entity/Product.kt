package com.sork.domain.entity

data class Product(
    val id: String,
    val imageUrl: String,
    val brandName: String,
    val description: String,
    val price: String,
    val purchaseUrl: String,
    val matchedSize: String,
    val detailSizes: List<DetailSize>
)