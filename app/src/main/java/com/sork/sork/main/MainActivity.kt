package com.sork.sork.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sork.domain.entity.ProductSummary
import com.sork.sork.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)

        val binding = binding ?: return

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ProductAdapter()
        binding.recyclerView.adapter = adapter

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetBinding.root)
        bottomSheetBehavior?.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomsheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> setupBottomSheetView(true)
                    BottomSheetBehavior.STATE_COLLAPSED -> setupBottomSheetView(false)
                    else -> return
                }
            }

            override fun onSlide(p0: View, slideOffset: Float) {}
        })

        binding.bottomSheetBinding.collapsedTitleLayout.setOnClickListener { openBottomSheet() }
        binding.bottomSheetBinding.negativeButton.setOnClickListener { closeBottomSheet() }
        binding.bottomSheetBackground.setOnClickListener { closeBottomSheet() }
        binding.enterMeasureButton.setOnClickListener { openBottomSheet() }
    }

    private fun openBottomSheet() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        setupBottomSheetView(true)
    }

    private fun closeBottomSheet() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        setupBottomSheetView(false)
    }

    private fun setupBottomSheetView(expanded: Boolean) {
        val binding = binding ?: return
        binding.bottomSheetBackground.visibility = if (expanded) View.VISIBLE else View.INVISIBLE
        binding.bottomSheetBackground.post {
            binding.bottomSheetBinding.collapsedTitleLayout.visibility = if (expanded) View.GONE else View.VISIBLE
            binding.bottomSheetBinding.expandedTitleLayout.visibility = if (expanded) View.VISIBLE else View.GONE
        }
    }
}