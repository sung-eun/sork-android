package com.sork.sork.main.bottomsheet.measurement

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sork.common.extension.setOnClickListenerWithHaptic
import com.sork.common.util.MeasurementUtil
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.sork.common.getMeasurementTypeName
import com.sork.sork.databinding.ViewMeasurementItemBinding
import com.sork.sork.main.bottomsheet.ruler.ItemPaddingDecoration
import com.sork.sork.main.bottomsheet.ruler.OnSnapPositionChangeListener
import com.sork.sork.main.bottomsheet.ruler.RulerAdapter
import com.sork.sork.main.bottomsheet.ruler.SnapOnScrollListener

interface GuideListener {
    fun onClickGuide(measurementType: MeasurementType)
}

private const val RULER_SIZE = 121

class MeasurementItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: ViewMeasurementItemBinding? = null

    private lateinit var rulerAdapter: RulerAdapter
    private lateinit var snapHelper: LinearSnapHelper

    private var measurement: Measurement? = null

    var guideListener: GuideListener? = null

    init {
        binding = ViewMeasurementItemBinding.inflate(LayoutInflater.from(context), this, true)

        initListeners()
        initRecyclerView()
    }

    private fun initListeners() {
        val binding = binding ?: return
        binding.labelLayout.setOnClickListenerWithHaptic {
            if (binding.title.isFocused) {
                binding.title.clearFocus()
            } else {
                binding.title.requestFocus()
            }
        }
        binding.title.setOnFocusChangeListener { _, focused ->
            binding.rulerGroup.visibility = if (focused) VISIBLE else GONE
            binding.guideButton.visibility = if (focused) VISIBLE else GONE

            if (focused && measurement != null) {
                val targetPosition = rulerAdapter.getPosition(getSelectedValue())
                scrollRulerToPosition(targetPosition)
            }
        }
        binding.guideButton.setOnClickListenerWithHaptic {
            if (measurement == null) return@setOnClickListenerWithHaptic
            guideListener?.onClickGuide(measurement!!.type)
        }
    }

    private fun scrollRulerToPosition(position: Int) {
        val binding = binding ?: return
        binding.value.isHapticFeedbackEnabled = false
        binding.rulerRecyclerView.scrollToPosition(position)
        binding.rulerRecyclerView.post {
            binding.rulerRecyclerView.layoutManager?.let { layoutManager ->
                val snapDistance: IntArray = layoutManager.findViewByPosition(position)?.let {
                    snapHelper.calculateDistanceToFinalSnap(
                        layoutManager,
                        it
                    )
                } ?: return@let

                if (snapDistance[0] != 0 || snapDistance[1] != 0) {
                    binding.rulerRecyclerView.scrollBy(snapDistance[0], snapDistance[1])
                }
                binding.value.isHapticFeedbackEnabled = true
            }
        }
    }

    private fun initRecyclerView() {
        val binding = binding ?: return

        rulerAdapter = RulerAdapter(RULER_SIZE)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rulerRecyclerView.layoutManager = layoutManager
        binding.rulerRecyclerView.adapter = rulerAdapter
        binding.rulerRecyclerView.addItemDecoration(ItemPaddingDecoration())

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rulerRecyclerView)

        val snapScrollListener =
            SnapOnScrollListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL, object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    val positionExceptHeader = position - 1
                    val value = positionExceptHeader / 2.toDouble()
                    binding.value.text = MeasurementUtil.getAdjustedValue(value, false)
                    binding.checkbox.isEnabled = value > 0 && isEnabled
                    if (value == 0.0) {
                        binding.checkbox.isChecked = false
                    }
                    binding.value.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            })

        binding.rulerRecyclerView.addOnScrollListener(snapScrollListener)
    }

    fun setMeasurement(measurement: Measurement) {
        this.measurement = measurement

        val binding = binding ?: return

        binding.title.setText(getMeasurementTypeName(measurement.type))
        binding.value.text = MeasurementUtil.getAdjustedValue(measurement.value, false)
        binding.checkbox.isChecked = measurement.selected && measurement.value > 0
        binding.checkbox.isEnabled = measurement.value > 0.0 && isEnabled
    }

    fun getMeasurement(): Measurement {
        return measurement!!.copy(value = getSelectedValue(), selected = binding?.checkbox?.isChecked ?: false)
    }

    private fun getSelectedValue(): Double {
        val binding = binding ?: return 0.0
        val text = binding.value.text
        return text?.toString()?.toDoubleOrNull() ?: 0.0
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding?.checkbox?.isEnabled = enabled && getSelectedValue() > 0
    }
}