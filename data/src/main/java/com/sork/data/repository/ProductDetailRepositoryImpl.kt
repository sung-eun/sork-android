package com.sork.data.repository

import com.sork.data.datasource.remote.ProductDetailRemoteDataSource
import com.sork.domain.entity.Product
import com.sork.domain.repository.ProductDetailRepository
import io.reactivex.rxjava3.core.Single

class ProductDetailRepositoryImpl(private val remoteDataSource: ProductDetailRemoteDataSource) : ProductDetailRepository {
    override fun getProductDetail(id: String): Single<Product> {
        return remoteDataSource.getProductDetail(id)
    }
}