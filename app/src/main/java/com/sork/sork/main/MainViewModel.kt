package com.sork.sork.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sork.domain.entity.Measurement
import com.sork.domain.usecase.MeasurementUseCase
import com.sork.sork.main.model.MeasurementParam
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.util.concurrent.CopyOnWriteArrayList

class MainViewModel(private val measurementUseCase: MeasurementUseCase) : ViewModel() {
    private val disposable = CompositeDisposable()
    private var cachedMeasurements: CopyOnWriteArrayList<Measurement> = CopyOnWriteArrayList()

    val measurementParam: MutableLiveData<MeasurementParam> = MutableLiveData()
    val error: MutableLiveData<Throwable> = MutableLiveData()

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
                .doOnSuccess { cachedMeasurements = CopyOnWriteArrayList(it.measurements) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onSuccess = { measurementParam.value = it },
                    onError = { error.value = it }
                )
        )
    }

    fun cacheMeasurementInput(measurement: Measurement) {
        cachedMeasurements = CopyOnWriteArrayList(Observable.fromIterable(cachedMeasurements)
            .map {
                if (it.type == measurement.type) {
                    it.copy(value = measurement.value)
                } else {
                    it
                }
            }
            .toList()
            .blockingGet())
    }

    fun clearInputCache() {
        disposable.add(
            Completable.fromAction { cachedMeasurements.clear() }
                .subscribe()
        )
    }

    fun saveMeasurementInput() {
        disposable.add(
            Single.just(cachedMeasurements.toList())
                .flatMapCompletable { measurementUseCase.setMeasurements(it) }
                .andThen(Completable.fromAction { loadMeasurements() })
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