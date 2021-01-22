package com.sork.data.datasource.remote

import com.sork.data.datasource.remote.api.ProductApi
import com.sork.data.datasource.remote.mapper.ProductMapper
import com.sork.domain.entity.Product
import io.reactivex.rxjava3.core.Single

class ProductDetailRemoteDataSourceImpl(private val api: ProductApi) : ProductDetailRemoteDataSource {
    override fun getProductDetail(id: String): Single<Product> {
        return api.getProduct(id)
            .map { ProductMapper.mapToProduct(id, it) }
    }
}