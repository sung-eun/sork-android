package com.sork.domain.repository

import com.sork.domain.entity.Product
import io.reactivex.rxjava3.core.Single

interface ProductDetailRepository {
    fun getProductDetail(id: String): Single<Product>
}