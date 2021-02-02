package com.sork.data.datasource.remote.api

import android.content.Context
import com.sork.data.R
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {
    fun getProductApi(context: Context): ProductApi {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.api_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(OkHttpClient())
            .build()
            .create(ProductApi::class.java)
    }
}