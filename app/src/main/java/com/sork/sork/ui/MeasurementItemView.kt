package com.sork.sork.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.sork.sork.databinding.ViewMeasurementItemBinding
import com.sork.sork.ui.ruler.RulerAdapter

class MeasurementItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var binding: ViewMeasurementItemBinding? = null

    init {
        binding = ViewMeasurementItemBinding.inflate(LayoutInflater.from(context), this, true)

        initListeners()
        initRecyclerView()
    }

    private fun initListeners() {
        binding?.labelLayout?.setOnClickListener {
            binding?.title?.requestFocus()
        }
        binding?.title?.setOnFocusChangeListener { _, focused ->
            binding?.rulerGroup?.visibility = if (focused) VISIBLE else GONE
            binding?.guideButton?.visibility = if (focused) VISIBLE else GONE
        }
    }

    private fun initRecyclerView() {
        val binding = binding ?: return

        val rulerAdapter = RulerAdapter(121)
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rulerRecyclerView.layoutManager = layoutManager
        binding.rulerRecyclerView.adapter = rulerAdapter

        val snapHelper = LinearSnapHelper()
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

                    if (moveTargetPosition == -1) {
                        binding.value.text = rulerAdapter.getVirtualPosition(position).toString()
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

                        binding.value.text = rulerAdapter.getVirtualPosition(moveTargetPosition).toString()
                    }
                }
            })
        binding.rulerRecyclerView.addOnScrollListener(snapOnScrollListener)
    }
}