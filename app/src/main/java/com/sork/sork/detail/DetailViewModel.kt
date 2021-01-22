package com.sork.sork.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.domain.entity.Product
import com.sork.domain.usecase.MeasurementUseCase
import com.sork.domain.usecase.ProductDetailUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class DetailViewModel(
    private val measurementUseCase: MeasurementUseCase,
    private val productDetailUseCase: ProductDetailUseCase
) : ViewModel() {
    private val disposable = CompositeDisposable()

    val measurements: MutableLiveData<Map<MeasurementType, Measurement>> = MutableLiveData()
    val product: MutableLiveData<Product> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<Throwable> = MutableLiveData()

    fun loadInitialData(id: String) {
        getMeasurements()
        getProductDetail(id)
    }

    private fun getProductDetail(id: String) {
        loading.value = true
        disposable.add(
            productDetailUseCase.getProductDetail(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        product.value = it
                        loading.value = false
                    },
                    onError = {
                        error.value = it
                        loading.value = false
                    }
                )
        )
    }

    private fun getMeasurements() {
        disposable.add(
            measurementUseCase.getMeasurements()
                .map { list -> list.map { it.type to it }.toMap() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        measurements.value = it
                    },
                    onError = {
                        error.value = it
                        loading.value = false
                    }
                )
        )
    }

    override fun onCleared() {
        disposable.clear()
    }
}

class DetailViewModelFactory(private val measurementUseCase: MeasurementUseCase, private val productDetailUseCase: ProductDetailUseCase) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DetailViewModel(measurementUseCase, productDetailUseCase) as T
    }
}