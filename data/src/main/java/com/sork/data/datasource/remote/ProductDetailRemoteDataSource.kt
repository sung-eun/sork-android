package com.sork.data.datasource.remote

import com.sork.domain.entity.Product
import io.reactivex.rxjava3.core.Single

interface ProductDetailRemoteDataSource {
    fun getProductDetail(id: String): Single<Product>
}