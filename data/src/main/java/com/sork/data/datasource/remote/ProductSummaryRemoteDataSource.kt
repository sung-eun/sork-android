package com.sork.data.datasource.remote

import com.sork.domain.entity.Measurement
import com.sork.domain.entity.ProductSummary
import io.reactivex.rxjava3.core.Single

interface ProductSummaryRemoteDataSource {
    fun getProductSummaries(): Single<List<ProductSummary>>
    fun getProductSummaries(measurements: List<Measurement>): Single<List<ProductSummary>>
}