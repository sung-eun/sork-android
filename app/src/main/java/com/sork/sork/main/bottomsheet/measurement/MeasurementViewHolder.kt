package com.sork.sork.main.bottomsheet.measurement

import androidx.recyclerview.widget.RecyclerView
import com.sork.domain.entity.Measurement
import com.sork.sork.databinding.HolderMeasurementBinding

class MeasurementViewHolder(
    private val binding: HolderMeasurementBinding,
    private val measurementChanged: (Measurement) -> Unit = {}
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.measurementChangedListener = object : MeasurementItemView.MeasurementChangedListener {
            override fun onChanged(value: Int) {
                measurement?.copy(value = value.toLong())?.let { measurementChanged(it) }
            }
        }
    }

    private var measurement: Measurement? = null

    fun bind(measurement: Measurement) {
        this.measurement = measurement
        binding.root.setMeasurement(measurement)
    }
}