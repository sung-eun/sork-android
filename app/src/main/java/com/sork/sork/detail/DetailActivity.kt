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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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
    private var viewModel: DetailViewModel? = null

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

        viewModel?.let {
            it.measurements.observe(this, { measurements ->
                if (measurements == null || it.product.value == null) return@observe
                bindMeasurements(measurements, it.product.value!!)
            })

            it.product.observe(this, { product ->
                if (product == null) return@observe
                bindProduct(product)
                if (it.measurements.value != null) {
                    bindMeasurements(it.measurements.value!!, product)
                }
            })

            it.loading.observe(this, { show ->
                if (show == null) return@observe
                setProgress(show)
            })

            it.error.observe(this, { throwable ->
                if (throwable == null) return@observe
                throwable.printStackTrace()
            })
        }

        val id = intent.getStringExtra(EXTRA_ID)
        if (id.isNullOrEmpty()) {
            finish()
            return
        }
        viewModel?.loadInitialData(id)
    }

    private fun initViews() {
        val binding = binding ?: return
        binding.backButton.setOnClickListenerWithHaptic { onBackPressed() }
        binding.purchaseButton.setOnClickListenerWithHaptic {
            viewModel?.product?.value?.let {
                launchWebBrowser(it.purchaseUrl)
            }
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
        binding.titleBrand.text = product.brandName

        Glide.with(this)
            .load(product.imageUrl)
            .transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.main_thumbnail_radius)))
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
}