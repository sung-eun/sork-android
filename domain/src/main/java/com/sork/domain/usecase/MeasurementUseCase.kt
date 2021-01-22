package com.sork.domain.usecase

import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.domain.entity.ProductSummary
import com.sork.domain.repository.MeasurementRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class MeasurementUseCase(private val repository: MeasurementRepository) {
    fun getMeasurements(): Single<List<Measurement>> {
        return repository.getMeasurements()
    }

    fun setMeasurements(measurements: List<Measurement>): Completable {
        return repository.setMeasurements(measurements)
    }

    fun getShortSleevesEmptyMeasurements(): Single<List<Measurement>> {
        return Single.fromCallable {
            listOf(
                Measurement(MeasurementType.SHOULDER_WIDTH, 0.0),
                Measurement(MeasurementType.SLEEVE_LENGTH, 0.0),
                Measurement(MeasurementType.BUST_WIDTH, 0.0),
                Measurement(MeasurementType.TOTAL_LENGTH, 0.0),
            )
        }
    }

    fun getProductSummaries(): Single<List<ProductSummary>> {
        return repository.getProductSummaries()
    }

    fun getProductSummaries(measurements: List<Measurement>): Single<List<ProductSummary>> {
        return repository.getProductSummaries(measurements)
    }
}