package com.sork.sork.main.bottomsheet

object MeasurementUtil {
    fun getAdjustedValue(value: Double): String {
        return value.toString().trimEnd('0').trimEnd('.')
    }
}