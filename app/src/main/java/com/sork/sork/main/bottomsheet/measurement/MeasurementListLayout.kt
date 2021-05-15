package com.sork.sork.main.bottomsheet.measurement

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.children
import com.sork.common.extension.setOnClickListenerWithHaptic
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.sork.R
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.CopyOnWriteArraySet

class MeasurementListLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
    }

    private val checkedMeasurementTypeSet = CopyOnWriteArraySet<MeasurementType>()

    private val checkedChangedListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (buttonView.tag == null) return@OnCheckedChangeListener

        val type = buttonView.tag as? MeasurementType ?: return@OnCheckedChangeListener

        if (isChecked) {
            checkedMeasurementTypeSet.add(type)
        } else {
            checkedMeasurementTypeSet.remove(type)
        }

        if (checkedMeasurementTypeSet.size >= 2) {
            disableUnSelectedMeasurements()
        } else {
            enableAllMeasurements()
        }
    }

    var guideListener: GuideListener? = null

    private fun disableUnSelectedMeasurements() {
        children.forEach { view ->
            (view as? MeasurementItemView)?.let {
                it.isEnabled = checkedMeasurementTypeSet.contains(it.tag)
            }
        }
    }

    private fun enableAllMeasurements() {
        children.forEach {
            (it as? MeasurementItemView)?.let { measurementItemView ->
                measurementItemView.isEnabled = true
            }
        }
    }

    fun setMeasurements(measurements: List<Measurement>) {
        checkedMeasurementTypeSet.clear()
        removeAllViews()

        measurements.forEach {
            val itemView = MeasurementItemView(context)
            itemView.guideListener = guideListener
            itemView.setMeasurement(it)
            itemView.tag = it.type

            val checkbox = itemView.findViewById<AppCompatCheckBox>(R.id.checkbox)
            checkbox.tag = it.type
            checkbox.setOnCheckedChangeListener(checkedChangedListener)
            checkbox.setOnClickListenerWithHaptic { }

            if (it.selected) {
                checkedMeasurementTypeSet.add(it.type)
            }

            addView(itemView)
        }

        if (checkedMeasurementTypeSet.size >= 2) {
            disableUnSelectedMeasurements()
        } else {
            enableAllMeasurements()
        }
    }

    fun getMeasurements(): List<Measurement> {
        return Observable.fromIterable(children.toList())
            .map { view -> (view as MeasurementItemView).getMeasurement() }
            .toList()
            .blockingGet()
    }
}