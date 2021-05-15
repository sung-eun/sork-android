package com.sork.sork.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.sork.common.extension.setOnClickListenerWithHaptic
import com.sork.data.datasource.local.MeasurementLocalDataSourceImpl
import com.sork.data.datasource.remote.ProductDetailRemoteDataSourceImpl
import com.sork.data.datasource.remote.ProductSummaryRemoteDataSourceImpl
import com.sork.data.datasource.remote.api.ApiFactory
import com.sork.data.repository.MeasurementRepositoryImpl
import com.sork.data.repository.ProductDetailRepositoryImpl
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.domain.entity.Product
import com.sork.domain.usecase.MeasurementUseCase
import com.sork.domain.usecase.ProductDetailUseCase
import com.sork.sork.R
import com.sork.sork.databinding.ActivityDetailBinding


const val EXTRA_ID = "com.sork.sork.detail.EXTRA_ID"

class DetailActivity : AppCompatActivity() {
    private var binding: ActivityDetailBinding? = null

    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(LayoutInflater.from(this))
        setContentView(binding?.root)

        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        val productApi = ApiFactory.getProductApi(this)
        viewModel = ViewModelProvider(
            this,
            DetailViewModelFactory(
                MeasurementUseCase(
                    MeasurementRepositoryImpl(
                        MeasurementLocalDataSourceImpl(this),
                        ProductSummaryRemoteDataSourceImpl(productApi)
                    )
                ),
                ProductDetailUseCase(ProductDetailRepositoryImpl(ProductDetailRemoteDataSourceImpl(productApi)))
            )
        ).get(DetailViewModel::class.java)

        viewModel.measurements.observe(this, { updateMeasurements(it) })
        viewModel.product.observe(this, { updateProduct(it) })
        viewModel.loading.observe(this, { setProgress(it) })
        viewModel.error.observe(this, { /*Do Nothing*/ })

        val id = intent.getStringExtra(EXTRA_ID)
        if (id.isNullOrEmpty()) {
            finish()
            return
        }

        viewModel.loadInitialData(id)
    }

    private fun initViews() {
        val binding = binding ?: return
        binding.backButton.setOnClickListenerWithHaptic { finish() }
        binding.purchaseButton.setOnClickListenerWithHaptic {
            viewModel.product.value?.let {
                launchWebBrowser(it.purchaseUrl)
            }
        }
    }

    private fun updateMeasurements(measurements: Map<MeasurementType, Measurement>?) {
        if (measurements.isNullOrEmpty()) return
        viewModel.product.value?.let {
            bindMeasurements(measurements, it)
        }
    }

    private fun updateProduct(product: Product?) {
        if (product == null) return
        bindProduct(product)
        viewModel.measurements.value?.let {
            bindMeasurements(it, product)
        }
    }

    private fun setProgress(visible: Boolean) {
        val binding = binding ?: return
        binding.progress.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun launchWebBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun bindProduct(product: Product) {
        val binding = binding ?: return

        Glide.with(this)
            .load(product.imageUrl)
            .transform(CenterCrop())
            .into(binding.thumbnail)

        binding.brandNameText.text = product.brandName
        binding.descriptionText.text = product.description
        binding.priceText.text = product.price
    }

    private fun bindMeasurements(measurements: Map<MeasurementType, Measurement>, product: Product) {
        val binding = binding ?: return

        if (measurements.isEmpty()) {
            binding.guideEnterMeasurement.visibility = View.VISIBLE
            return
        }

        binding.detailSizeInfoBinding.measurementListLayout.removeAllViews()
        product.detailSizes.forEach {
            val itemView = DetailMeasurementView(this)
            val measurement = measurements[it.type]
            if (measurement == null) {
                itemView.setMeasurement(0.0, it)
            } else {
                itemView.setMeasurement(measurement.value, it)
            }
            binding.detailSizeInfoBinding.measurementListLayout.addView(itemView)
        }

        binding.detailSizeInfoBinding.matchedSizeText.text = product.matchedSize
        binding.purchaseButton.text = getString(R.string.purchase_matched_size, product.matchedSize)
        binding.detailSizeInfoBinding.root.visibility = View.VISIBLE
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}