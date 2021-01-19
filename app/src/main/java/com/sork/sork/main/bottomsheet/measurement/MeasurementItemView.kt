package com.sork.sork.main.bottomsheet.measurement

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.sork.R
import com.sork.sork.databinding.ViewMeasurementItemBinding
import com.sork.sork.main.bottomsheet.ruler.RulerAdapter
import com.sork.sork.ui.OnSnapPositionChangeListener
import com.sork.sork.ui.SnapOnScrollListener

class MeasurementItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    interface MeasurementChangedListener {
        fun onChanged(value: Int)
    }

    private var binding: ViewMeasurementItemBinding? = null

    private lateinit var rulerAdapter: RulerAdapter
    private lateinit var snapHelper: LinearSnapHelper

    var measurementChangedListener: MeasurementChangedListener? = null

    init {
        binding = ViewMeasurementItemBinding.inflate(LayoutInflater.from(context), this, true)

        initListeners()
        initRecyclerView()
    }

    private fun initListeners() {
        val binding = binding ?: return
        binding.labelLayout.setOnClickListener {
            if (binding.title.isFocused) {
                binding.title.clearFocus()
            } else {
                binding.title.requestFocus()
            }
        }
        binding.title.setOnFocusChangeListener { _, focused ->
            binding.rulerGroup.visibility = if (focused) VISIBLE else GONE
            binding.guideButton.visibility = if (focused) VISIBLE else GONE

            if (focused) {
                val targetPosition = rulerAdapter.getPosition(binding.value.text.toString().toInt())
                binding.rulerRecyclerView.scrollToPosition(targetPosition)
                binding.rulerRecyclerView.post {
                    binding.rulerRecyclerView.layoutManager?.let { layoutManager ->
                        val snapDistance: IntArray = layoutManager.findViewByPosition(targetPosition)?.let {
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
        }
    }

    private fun initRecyclerView() {
        val binding = binding ?: return

        rulerAdapter = RulerAdapter(121)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rulerRecyclerView.layoutManager = layoutManager
        binding.rulerRecyclerView.adapter = rulerAdapter

        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rulerRecyclerView)

        val snapOnScrollListener =
            SnapOnScrollListener(snapHelper, SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE, object : OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    val positionExceptHeader = position - 1
                    var moveTargetPosition = -1

                    if (positionExceptHeader >= 2 && (positionExceptHeader + 5) % 7 == 0) {
                        moveTargetPosition = position - 1
                    } else if (positionExceptHeader >= 5 && (positionExceptHeader + 2) % 7 == 0) {
                        moveTargetPosition = position + 1
                    }

                    val newValue: Int
                    if (moveTargetPosition == -1) {
                        newValue = rulerAdapter.getVirtualPosition(position)
                    } else {
                        val snapDistance: IntArray = layoutManager.findViewByPosition(moveTargetPosition)?.let {
                            snapHelper.calculateDistanceToFinalSnap(
                                layoutManager,
                                it
                            )
                        } ?: return

                        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
                            binding.rulerRecyclerView.scrollBy(snapDistance[0], snapDistance[1])
                        }

                        newValue = rulerAdapter.getVirtualPosition(moveTargetPosition)
                    }
                    binding.value.text = newValue.toString()
                    measurementChangedListener?.onChanged(newValue)
                }
            })
        binding.rulerRecyclerView.addOnScrollListener(snapOnScrollListener)
    }

    fun setMeasurement(measurement: Measurement) {
        val binding = binding ?: return
        binding.title.setText(getMeasurementTypeName(measurement.type))
        binding.value.text = measurement.value.toString()
    }

    private fun getMeasurementTypeName(type: MeasurementType): Int {
        return when (type) {
            MeasurementType.SHOULDER_WIDTH -> R.string.shoulder_width
            MeasurementType.SLEEVE_LENGTH -> R.string.sleeve_length
            MeasurementType.BUST_WIDTH -> R.string.bust_width
            MeasurementType.TOTAL_LENGTH -> R.string.total_length
        }
    }
}