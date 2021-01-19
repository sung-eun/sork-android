package com.sork.sork.main.bottomsheet.measurement

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sork.domain.entity.Measurement
import com.sork.sork.databinding.HolderMeasurementBinding

class MeasurementAdapter(private val measurementChanged: (Measurement) -> Unit = {}) :
    ListAdapter<Measurement, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Measurement>() {
        override fun areItemsTheSame(oldItem: Measurement, newItem: Measurement): Boolean {
            return oldItem.type == newItem.type
        }

        override fun areContentsTheSame(oldItem: Measurement, newItem: Measurement): Boolean {
            return oldItem == newItem
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MeasurementViewHolder(
            HolderMeasurementBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            measurementChanged
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? MeasurementViewHolder)?.bind(getItem(position))
    }
}