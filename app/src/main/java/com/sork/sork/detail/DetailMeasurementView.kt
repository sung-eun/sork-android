package com.sork.sork.detail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.sork.common.util.MeasurementUtil
import com.sork.domain.entity.DetailSize
import com.sork.domain.entity.MeasurementType
import com.sork.sork.R
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

    private fun getMeasurementTypeName(type: MeasurementType): Int {
        return when (type) {
            MeasurementType.SHOULDER_WIDTH -> R.string.shoulder_width
            MeasurementType.SLEEVE_LENGTH -> R.string.sleeve_length
            MeasurementType.BUST_WIDTH -> R.string.bust_width
            MeasurementType.TOTAL_LENGTH -> R.string.total_length
        }
    }

    private fun setNonValueMessage() {
        val binding = binding ?: return
        binding.message.setText(R.string.label_none_value)
        binding.message.setTextColor(ContextCompat.getColor(context, R.color.color_cc))
    }

    private fun setDifferenceMessage(difference: Double) {
        val binding = binding ?: return

        if (difference == 0.0) {
            binding.message.setText(R.string.label_suitable)
            binding.message.setTextColor(ContextCompat.getColor(context, R.color.green))
        } else if (difference > 0) {
            binding.message.text = context.getString(R.string.label_lager_than, MeasurementUtil.getAdjustedValue(difference, true))
            binding.message.setTextColor(ContextCompat.getColor(context, R.color.red))
        } else {
            binding.message.text = context.getString(R.string.label_smaller_than, MeasurementUtil.getAdjustedValue(-difference, true))
            binding.message.setTextColor(ContextCompat.getColor(context, R.color.red))
        }
    }
}