package com.sork.common.util

object MeasurementUtil {
    fun getAdjustedValue(value: Double, trimZero: Boolean): String {
        if (value == 0.0) {
            return "0"
        } else if (trimZero) {
            return value.toString().trimEnd('0').trimEnd('.')
        }
        return value.toString()
    }
}