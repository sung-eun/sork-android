package com.sork.domain.usecase

import com.sork.domain.entity.Product
import com.sork.domain.repository.ProductDetailRepository
import io.reactivex.rxjava3.core.Single

class ProductDetailUseCase(private val repository: ProductDetailRepository) {
    fun getProductDetail(id: String): Single<Product> {
        return repository.getProductDetail(id)
    }
}