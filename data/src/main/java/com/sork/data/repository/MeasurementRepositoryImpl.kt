package com.sork.data.repository

import com.sork.data.datasource.local.MeasurementLocalDataSource
import com.sork.data.datasource.remote.ProductSummaryRemoteDataSource
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.ProductSummary
import com.sork.domain.repository.MeasurementRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class MeasurementRepositoryImpl(
    private val localDataSource: MeasurementLocalDataSource,
    private val remoteDataSource: ProductSummaryRemoteDataSource
) : MeasurementRepository {

    override fun getMeasurements(): Single<List<Measurement>> {
        return localDataSource.getMeasurements()
    }

    override fun setMeasurements(measurements: List<Measurement>): Completable {
        return localDataSource.setMeasurements(measurements)
    }

    override fun getProductSummaries(): Single<List<ProductSummary>> {
        return remoteDataSource.getProductSummaries()
    }

    override fun getProductSummaries(measurements: List<Measurement>): Single<List<ProductSummary>> {
        return remoteDataSource.getProductSummaries(measurements)
    }
}