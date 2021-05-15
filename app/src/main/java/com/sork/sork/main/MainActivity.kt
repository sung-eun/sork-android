package com.sork.sork.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.sork.common.extension.setOnClickListenerWithHaptic
import com.sork.common.util.MeasurementUtil
import com.sork.data.datasource.local.MeasurementLocalDataSourceImpl
import com.sork.data.datasource.remote.ProductSummaryRemoteDataSourceImpl
import com.sork.data.datasource.remote.api.ApiFactory
import com.sork.data.repository.MeasurementRepositoryImpl
import com.sork.domain.entity.MeasurementType
import com.sork.domain.entity.ProductSummary
import com.sork.domain.usecase.MeasurementUseCase
import com.sork.sork.R
import com.sork.sork.common.getMeasurementTypeName
import com.sork.sork.databinding.ActivityMainBinding
import com.sork.sork.detail.DetailActivity
import com.sork.sork.detail.EXTRA_ID
import com.sork.sork.main.bottomsheet.measurement.GuideListener
import com.sork.sork.main.model.MeasurementParam

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private lateinit var viewModel: MainViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)

        initViewModel()
        initViews()

        viewModel.loadInitialData()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                MeasurementUseCase(
                    MeasurementRepositoryImpl(
                        MeasurementLocalDataSourceImpl(this),
                        ProductSummaryRemoteDataSourceImpl(ApiFactory.getProductApi(this))
                    )
                )
            )
        ).get(MainViewModel::class.java)

        viewModel.productSummaries.observe(this, { updateProductSummaries(it) })
        viewModel.measurementParam.observe(this, { setBottomSheetMeasurement(it) })
        viewModel.loading.observe(this, { setProgress(it) })
        viewModel.error.observe(this, {/*Currently, do nothing*/ })
    }

    private fun initViews() {
        val binding = binding ?: return

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = ProductAdapter { id -> goDetail(id) }
        binding.recyclerView.adapter = productAdapter

        initBottomSheet()
    }

    private fun initBottomSheet() {
        val binding = binding ?: return

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
        binding.bottomSheetBinding.collapsedTitleLayout.setOnClickListenerWithHaptic { openBottomSheet() }
        binding.bottomSheetBackground.setOnClickListenerWithHaptic { cancelEditMeasurement() }
        binding.enterMeasureButton.setOnClickListenerWithHaptic { openBottomSheet() }

        binding.bottomSheetBinding.negativeButton.setOnClickListenerWithHaptic { cancelEditMeasurement() }
        binding.bottomSheetBinding.positiveButton.setOnClickListenerWithHaptic {
            closeBottomSheet()
            viewModel.changeMeasurementInput(binding.bottomSheetBinding.measurementListLayout.getMeasurements())
        }
        binding.bottomSheetBinding.measurementListLayout.guideListener = object : GuideListener {
            override fun onClickGuide(measurementType: MeasurementType) {
                showSizeMeasureGuide(measurementType)
            }
        }
        binding.bottomSheetBinding.guideBinding.guideConfirmButton.setOnClickListenerWithHaptic {
            closeSizeMeasureGuide()
        }
    }

    private fun setProgress(visible: Boolean) {
        val binding = binding ?: return
        binding.progress.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateProductSummaries(productSummaries: List<ProductSummary>) {
        val binding = binding ?: return

        binding.emptyText.visibility = if (productSummaries.isEmpty()) View.VISIBLE else View.GONE

        productAdapter.submitList(productSummaries) {
            binding.recyclerView.scrollToPosition(0)
            binding.recyclerView.postDelayed({
                binding.appbar.setExpanded(true, false)
            }, 300)
        }
    }


    private fun cancelEditMeasurement() {
        closeBottomSheet()
        setBottomSheetMeasurement(viewModel.measurementParam.value)
    }

    private fun openBottomSheet() {
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_EXPANDED
        setupBottomSheetView(true)
    }

    private fun closeBottomSheet() {
        closeSizeMeasureGuide()
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

    private fun setBottomSheetMeasurement(measurementParam: MeasurementParam?) {
        if (measurementParam == null) return

        val binding = binding ?: return

        setTopHeaderStatus(measurementParam.hasSavedMeasurements)

        if (measurementParam.hasSavedMeasurements) {
            val stringBuilder = StringBuilder()
            measurementParam.measurements.forEach {
                stringBuilder
                    .append(' ')
                    .append(MeasurementUtil.getAdjustedValue(it.value, true))
            }
            binding.bottomSheetBinding.collapsedTitle.text = getString(R.string.short_sleeves_measurement_summary, stringBuilder.toString())
        } else {
            binding.bottomSheetBinding.collapsedTitle.setText(R.string.message_enter_measure)
        }

        binding.bottomSheetBinding.measurementListLayout.setMeasurements(measurementParam.measurements)
    }

    private fun showSizeMeasureGuide(measurementType: MeasurementType) {
        val binding = binding ?: return
        binding.bottomSheetBinding.guideBinding.guideTypeName.setText(getMeasurementTypeName(measurementType))
        binding.bottomSheetBinding.guideBinding.guideDescription.setText(getMeasurementTypeDescription(measurementType))
        binding.bottomSheetBinding.guideBinding.guideImage.setImageResource(getMeasurementTypeImage(measurementType))
        binding.bottomSheetBinding.guideBinding.root.visibility = View.VISIBLE
        binding.bottomSheetBinding.buttonGroup.visibility = View.GONE
    }

    private fun closeSizeMeasureGuide() {
        val binding = binding ?: return
        binding.bottomSheetBinding.guideBinding.root.visibility = View.GONE
        binding.bottomSheetBinding.buttonGroup.visibility = View.VISIBLE
    }

    private fun getMeasurementTypeDescription(type: MeasurementType): Int {
        return when (type) {
            MeasurementType.SHOULDER_WIDTH -> R.string.description_shoulder_width
            MeasurementType.SLEEVE_LENGTH -> R.string.description_sleeve_length
            MeasurementType.BUST_WIDTH -> R.string.description_bust_width
            MeasurementType.TOTAL_LENGTH -> R.string.description_total_length
        }
    }

    private fun getMeasurementTypeImage(type: MeasurementType): Int {
        return when (type) {
            MeasurementType.SHOULDER_WIDTH -> R.drawable.img_sample_tshirt_shoulder
            MeasurementType.SLEEVE_LENGTH -> R.drawable.img_sample_tshirt_arm
            MeasurementType.BUST_WIDTH -> R.drawable.img_sample_tshirt_chest
            MeasurementType.TOTAL_LENGTH -> R.drawable.img_sample_tshirt_length
        }
    }

    private fun setTopHeaderStatus(hasSavedMeasurements: Boolean) {
        val binding = binding ?: return
        if (hasSavedMeasurements) {
            binding.enterMeasureButton.visibility = View.GONE
            binding.messageHasMeasurements.visibility = View.VISIBLE
            binding.titleIcon.setImageResource(R.drawable.emoji_hand_attention)
        } else {
            binding.enterMeasureButton.visibility = View.VISIBLE
            binding.messageHasMeasurements.visibility = View.GONE
            binding.titleIcon.setImageResource(R.drawable.emoji_write)
        }
    }

    private fun goDetail(id: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(EXTRA_ID, id)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP.or(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    override fun onBackPressed() {
        if (binding != null && binding!!.bottomSheetBinding.guideBinding.root.visibility == View.VISIBLE) {
            closeSizeMeasureGuide()
            return
        }
        if (bottomSheetBehavior != null && bottomSheetBehavior!!.state == BottomSheetBehavior.STATE_EXPANDED) {
            cancelEditMeasurement()
            return
        }
        super.onBackPressed()
    }
}