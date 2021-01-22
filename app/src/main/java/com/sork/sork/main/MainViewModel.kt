package com.sork.sork.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.ProductSummary
import com.sork.domain.usecase.MeasurementUseCase
import com.sork.sork.main.model.MeasurementParam
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy

class MainViewModel(private val measurementUseCase: MeasurementUseCase) : ViewModel() {
    private val disposable = CompositeDisposable()

    val measurementParam: MutableLiveData<MeasurementParam> = MutableLiveData()
    val productSummaries: MutableLiveData<List<ProductSummary>> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<Throwable> = MutableLiveData()

    fun loadInitialData() {
        loading.value = true
        disposable.add(
            measurementUseCase.getMeasurements()
                .flatMap { measurements ->
                    if (measurements.isEmpty()) {
                        measurementUseCase.getShortSleevesEmptyMeasurements()
                            .map { MeasurementParam(it, false) }
                    } else {
                        Single.just(MeasurementParam(measurements, true))
                    }
                }
                .doOnSuccess { param -> measurementParam.postValue(param) }
                .flatMap { param ->
                    if (param.hasSavedMeasurements) {
                        measurementUseCase.getProductSummaries(param.measurements)
                    } else {
                        measurementUseCase.getProductSummaries()
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = {
                        productSummaries.value = it
                        loading.value = false
                    },
                    onError = {
                        error.value = it
                        loading.value = false
                    }
                )
        )

    }

    fun loadMeasurements() {
        disposable.add(
            measurementUseCase.getMeasurements()
                .flatMap { measurements ->
                    if (measurements.isEmpty()) {
                        measurementUseCase.getShortSleevesEmptyMeasurements()
                            .map { MeasurementParam(it, false) }
                    } else {
                        Single.just(MeasurementParam(measurements, true))
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { measurementParam.value = it },
                    onError = { error.value = it }
                )
        )
    }

    fun changeMeasurementInput(measurements: List<Measurement>) {
        disposable.add(
            measurementUseCase.setMeasurements(measurements)
                .andThen(Completable.fromAction { loadInitialData() })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    override fun onCleared() {
        disposable.clear()
    }
}

class MainViewModelFactory(private val measurementUseCase: MeasurementUseCase) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(measurementUseCase) as T
    }
}