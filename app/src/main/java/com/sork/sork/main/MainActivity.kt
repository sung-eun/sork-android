package com.sork.sork.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sork.data.datasource.MeasurementLocalDataSourceImpl
import com.sork.data.repository.MeasurementRepositoryImpl
import com.sork.domain.entity.ProductSummary
import com.sork.domain.usecase.MeasurementUseCase
import com.sork.sork.R
import com.sork.sork.databinding.ActivityMainBinding
import com.sork.sork.main.bottomsheet.measurement.MeasurementAdapter
import com.sork.sork.main.model.MeasurementParam

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null
    private var bottomSheetMeasurementAdapter: MeasurementAdapter? = null

    private var viewModel: MainViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)

        initViewModel()
        initViews()

        viewModel?.loadMeasurements()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, MainViewModelFactory(MeasurementUseCase(MeasurementRepositoryImpl(MeasurementLocalDataSourceImpl(this)))))
            .get(MainViewModel::class.java)

        viewModel?.let {
            it.measurementParam.observe(this, { measurementParam ->
                if (measurementParam == null) return@observe
                setBottomSheetMeasurement(measurementParam)
            })
        }
    }

    private fun initViews() {
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

        binding.bottomSheetBinding.root.setOnClickListener { }
        binding.bottomSheetBinding.collapsedTitleLayout.setOnClickListener { openBottomSheet() }
        binding.bottomSheetBinding.negativeButton.setOnClickListener { closeBottomSheet() }
        binding.bottomSheetBackground.setOnClickListener { closeBottomSheet() }
        binding.enterMeasureButton.setOnClickListener { openBottomSheet() }

        binding.bottomSheetBinding.positiveButton.setOnClickListener {
            closeBottomSheet(false)
            viewModel?.saveMeasurementInput()
        }

        bottomSheetMeasurementAdapter = MeasurementAdapter(measurementChanged = { viewModel?.cacheMeasurementInput(it) })
        binding.bottomSheetBinding.measurementRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bottomSheetBinding.measurementRecyclerView.adapter = bottomSheetMeasurementAdapter
    }

    private fun openBottomSheet() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        setupBottomSheetView(true)
        bottomSheetMeasurementAdapter?.notifyDataSetChanged()
    }

    private fun closeBottomSheet(clearCache: Boolean = true) {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        setupBottomSheetView(false)
        if (clearCache) {
            viewModel?.clearInputCache()
        }
    }

    private fun setupBottomSheetView(expanded: Boolean) {
        val binding = binding ?: return
        binding.bottomSheetBackground.visibility = if (expanded) View.VISIBLE else View.INVISIBLE
        binding.bottomSheetBackground.post {
            binding.bottomSheetBinding.collapsedTitleLayout.visibility = if (expanded) View.GONE else View.VISIBLE
            binding.bottomSheetBinding.expandedTitleLayout.visibility = if (expanded) View.VISIBLE else View.GONE
        }
    }

    private fun setBottomSheetMeasurement(measurementParam: MeasurementParam) {
        val binding = binding ?: return

        if (measurementParam.hasSavedMeasurements) {
            val stringBuilder = StringBuilder()
            measurementParam.measurements.forEach {
                stringBuilder
                    .append(' ')
                    .append(it.value)
            }
            binding.bottomSheetBinding.collapsedTitle.text = getString(R.string.short_sleeves_measurement_summary, stringBuilder.toString())
        } else {
            binding.bottomSheetBinding.collapsedTitle.setText(R.string.message_enter_measure)
        }

        bottomSheetMeasurementAdapter?.submitList(measurementParam.measurements)
    }
}