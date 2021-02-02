package com.sork.sork.main.bottomsheet.measurement

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sork.common.extension.setOnClickListenerWithHaptic
import com.sork.common.util.MeasurementUtil
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.sork.R
import com.sork.sork.databinding.ViewMeasurementItemBinding
import com.sork.sork.main.bottomsheet.ruler.ItemPaddingDecoration
import com.sork.sork.main.bottomsheet.ruler.RulerAdapter
import com.sork.sork.ui.OnSnapPositionChangeListener
import com.sork.sork.ui.SnapOnScrollListener

class MeasurementItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding: ViewMeasurementItemBinding? = null

    private lateinit var rulerAdapter: RulerAdapter
    private lateinit var snapHelper: LinearSnapHelper

    private var measurement: Measurement? = null

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
                val targetPosition = rulerAdapter.getPosition(measurement!!.value)
                scrollRulerToPosition(targetPosition)
            }
        }
    }

    private fun scrollRulerToPosition(position: Int) {
        val binding = binding ?: return

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
            }
        }
    }

    private fun initRecyclerView() {
        val binding = binding ?: return

        rulerAdapter = RulerAdapter(121)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rulerRecyclerView.layoutManager = layoutManager
        binding.rulerRecyclerView.adapter = rulerAdapter
        binding.rulerRecyclerView.addItemDecoration(ItemPaddingDecoration())

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rulerRecyclerView)

        val snapOnScrollListener =
            SnapOnScrollListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE, object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    val positionExceptHeader = position - 1
                    val value = positionExceptHeader / 2.toDouble()
                    binding.value.text = MeasurementUtil.getAdjustedValue(value, false)
                    binding.checkbox.isEnabled = value > 0.0

                    Toast.makeText(context, value.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        binding.rulerRecyclerView.addOnScrollListener(snapOnScrollListener)
    }

    fun setMeasurement(measurement: Measurement) {
        this.measurement = measurement

        val binding = binding ?: return
        binding.title.setText(getMeasurementTypeName(measurement.type))
        binding.value.text = MeasurementUtil.getAdjustedValue(measurement.value, false)
        binding.checkbox.isChecked = measurement.selected
        binding.checkbox.isEnabled = measurement.value > 0.0
    }

    private fun getMeasurementTypeName(type: MeasurementType): Int {
        return when (type) {
            MeasurementType.SHOULDER_WIDTH -> R.string.shoulder_width
            MeasurementType.SLEEVE_LENGTH -> R.string.sleeve_length
            MeasurementType.BUST_WIDTH -> R.string.bust_width
            MeasurementType.TOTAL_LENGTH -> R.string.total_length
        }
    }

    fun getMeasurement(): Measurement {
        return measurement!!.copy(value = getSelectedValue(), selected = binding?.checkbox?.isChecked ?: false)
    }

    fun getSelectedValue(): Double {
        val binding = binding ?: return 0.0

        if (TextUtils.isEmpty(binding.value.text)) {
            return 0.0
        }

        return try {
            binding.value.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        binding?.checkbox?.isEnabled = enabled
    }

    fun setChecked(checked: Boolean) {
        binding?.checkbox?.isChecked = checked
    }
}