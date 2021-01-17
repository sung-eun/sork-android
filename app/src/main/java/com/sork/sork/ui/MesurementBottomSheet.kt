package com.sork.sork.ui;

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sork.sork.databinding.BottomSheetEnterMeasureBinding

class MesurementBottomSheet @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CoordinatorLayout(context, attrs, defStyleAttr) {
    private var binding: BottomSheetEnterMeasureBinding? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    init {
        binding = BottomSheetEnterMeasureBinding.inflate(LayoutInflater.from(context), this, true)
        binding?.let {
            bottomSheetBehavior = BottomSheetBehavior.from(it.root)
        }

        setBottomSheetCallback()
    }

    private fun setBottomSheetCallback() {
        val binding = binding ?: return
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomsheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_SETTLING -> {
                        if (binding.expandedTitleLayout.visibility == View.GONE) {
                            binding.collapsedTitleLayout.visibility = View.GONE
                            binding.expandedTitleLayout.visibility = View.VISIBLE
                        } else {
                            binding.collapsedTitleLayout.visibility = View.VISIBLE
                            binding.expandedTitleLayout.visibility = View.GONE
                        }
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.collapsedTitleLayout.visibility = View.GONE
                        binding.expandedTitleLayout.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.collapsedTitleLayout.visibility = View.VISIBLE
                        binding.expandedTitleLayout.visibility = View.GONE
                    }

                    else -> {
                    }
                }
            }

            override fun onSlide(p0: View, slideOffset: Float) {}
        })

        binding.collapsedTitleLayout.setOnClickListener { bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED }
        binding.negativeButton.setOnClickListener { bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED }
    }
}
