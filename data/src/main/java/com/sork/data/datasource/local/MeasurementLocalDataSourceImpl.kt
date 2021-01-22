package com.sork.data.datasource.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sork.domain.BuildConfig
import com.sork.domain.entity.Measurement
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

private const val PREF_NAME = BuildConfig.LIBRARY_PACKAGE_NAME + ".Measurement"
private const val PREF_KEY_SHORT_SLEEVES_MEASUREMENTS = "PREF_KEY_SHORT_SLEEVES_MEASUREMENTS"

class MeasurementLocalDataSourceImpl(context: Context) : MeasurementLocalDataSource {
    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun getMeasurements(): Single<List<Measurement>> {
        return Single.fromCallable {
            val measurementsJsonString = preferences.getString(PREF_KEY_SHORT_SLEEVES_MEASUREMENTS, "[]")
            Gson().fromJson(measurementsJsonString, object : TypeToken<List<Measurement>>() {}.type) as List<Measurement>
        }.subscribeOn(Schedulers.io())
    }

    override fun setMeasurements(measurements: List<Measurement>): Completable {
        return Completable.fromCallable {
            preferences.edit()
                .putString(PREF_KEY_SHORT_SLEEVES_MEASUREMENTS, Gson().toJson(measurements))
                .commit()
        }
    }
}