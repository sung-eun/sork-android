package com.sork.domain.repository

import com.sork.domain.entity.Measurement
import com.sork.domain.entity.ProductSummary
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface MeasurementRepository {
    fun getMeasurements(): Single<List<Measurement>>
    fun setMeasurements(measurements: List<Measurement>): Completable
    fun getProductSummaries(): Single<List<ProductSummary>>
    fun getProductSummaries(measurements: List<Measurement>): Single<List<ProductSummary>>
}