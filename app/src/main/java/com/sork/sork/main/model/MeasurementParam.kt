package com.sork.sork.main.model

import com.sork.domain.entity.Measurement

data class MeasurementParam(
    val measurements: List<Measurement>,
    val hasSavedMeasurements: Boolean
)
