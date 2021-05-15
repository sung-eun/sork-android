package com.sork.sork.common

import androidx.annotation.StringRes
import com.sork.domain.entity.MeasurementType
import com.sork.sork.R

@StringRes
fun getMeasurementTypeName(type: MeasurementType): Int {
    return when (type) {
        MeasurementType.SHOULDER_WIDTH -> R.string.shoulder_width
        MeasurementType.SLEEVE_LENGTH -> R.string.sleeve_length
        MeasurementType.BUST_WIDTH -> R.string.bust_width
        MeasurementType.TOTAL_LENGTH -> R.string.total_length
    }
}