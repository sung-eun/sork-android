package com.sork.sork.detail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.sork.common.util.MeasurementUtil
import com.sork.domain.entity.DetailSize
import com.sork.sork.R
import com.sork.sork.common.getMeasurementTypeName
import com.sork.sork.databinding.ViewDetailMeasurementItemBinding

class DetailMeasurementView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: ViewDetailMeasurementItemBinding? = null

    init {
        binding = ViewDetailMeasurementItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setMeasurement(inputMeasurementValue: Double, detailSize: DetailSize) {
        val binding = binding ?: return

        binding.typeName.setText(getMeasurementTypeName(detailSize.type))
        binding.value.text = context.getString(R.string.cm_with_value, MeasurementUtil.getAdjustedValue(detailSize.value, true))

        if (inputMeasurementValue == 0.0) {
            setNonValueMessage()
        } else {
            setDifferenceMessage(detailSize.value - inputMeasurementValue)
        }
    }

    private fun setNonValueMessage() {
        val binding = binding ?: return
        binding.message.setText(R.string.label_none_value)
        binding.message.setTextColor(ContextCompat.getColor(context, R.color.color_cc))
    }

    private fun setDifferenceMessage(difference: Double) {
        val binding = binding ?: return

        val (message, colorRes) = when {
            difference == 0.0 -> Pair(context.getString(R.string.label_suitable), R.color.green)
            difference > 0 -> Pair(context.getString(R.string.label_lager_than, MeasurementUtil.getAdjustedValue(difference, true)), R.color.red)
            else -> Pair(context.getString(R.string.label_smaller_than, MeasurementUtil.getAdjustedValue(-difference, true)), R.color.red)
        }

        binding.message.text = message
        binding.message.setTextColor(ContextCompat.getColor(context, colorRes))
    }
}