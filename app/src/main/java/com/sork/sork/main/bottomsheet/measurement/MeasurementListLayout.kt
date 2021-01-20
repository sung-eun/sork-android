package com.sork.sork.main.bottomsheet.measurement

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.sork.domain.entity.Measurement

class MeasurementListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
    }

    fun setMeasurements(measurements: List<Measurement>) {
        removeAllViews()

        measurements.forEach {
            val itemView = MeasurementItemView(context)
            itemView.setMeasurement(it)
            addView(itemView)
        }
    }
}