package com.sork.domain.repository

import com.sork.domain.entity.Measurement
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface MeasurementRepository {
    fun getMeasurements(): Single<List<Measurement>>
    fun setMeasurements(measurements: List<Measurement>): Completable
}