package com.sork.domain

data class ProductSummary(
    val id: String,
    val imageUrl: String,
    val brandName: String,
    val description: String,
    val price: Long
)