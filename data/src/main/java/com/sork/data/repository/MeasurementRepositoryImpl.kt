package com.sork.data.repository

import com.sork.data.datasource.MeasurementLocalDataSource
import com.sork.domain.entity.Measurement
import com.sork.domain.repository.MeasurementRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class MeasurementRepositoryImpl(private val localDataSource: MeasurementLocalDataSource) : MeasurementRepository {
    override fun getMeasurements(): Single<List<Measurement>> {
        return localDataSource.getMeasurements()
    }

    override fun setMeasurements(measurements: List<Measurement>): Completable {
        return localDataSource.setMeasurements(measurements)
    }
}