package com.sork.sork.di

import android.content.Context
import com.sork.data.R
import com.sork.data.datasource.local.MeasurementLocalDataSource
import com.sork.data.datasource.local.MeasurementLocalDataSourceImpl
import com.sork.data.datasource.remote.ProductDetailRemoteDataSource
import com.sork.data.datasource.remote.ProductDetailRemoteDataSourceImpl
import com.sork.data.datasource.remote.ProductSummaryRemoteDataSource
import com.sork.data.datasource.remote.ProductSummaryRemoteDataSourceImpl
import com.sork.data.datasource.remote.api.ProductApi
import com.sork.data.repository.MeasurementRepositoryImpl
import com.sork.data.repository.ProductDetailRepositoryImpl
import com.sork.domain.repository.MeasurementRepository
import com.sork.domain.repository.ProductDetailRepository
import com.sork.domain.usecase.MeasurementUseCase
import com.sork.domain.usecase.ProductDetailUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {
    @ViewModelScoped
    @Provides
    fun provideMeasurementUseCase(measurementRepository: MeasurementRepository): MeasurementUseCase {
        return MeasurementUseCase(measurementRepository)
    }

    @ViewModelScoped
    @Provides
    fun provideProductDetailUseCase(productDetailRepository: ProductDetailRepository): ProductDetailUseCase {
        return ProductDetailUseCase(productDetailRepository)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideMeasurementRepository(
        measurementLocalDataSource: MeasurementLocalDataSource,
        productSummaryRemoteDataSource: ProductSummaryRemoteDataSource
    ): MeasurementRepository {
        return MeasurementRepositoryImpl(measurementLocalDataSource, productSummaryRemoteDataSource)
    }

    @Singleton
    @Provides
    fun provideProductDetailRepository(productDetailRemoteDataSource: ProductDetailRemoteDataSource): ProductDetailRepository {
        return ProductDetailRepositoryImpl(productDetailRemoteDataSource)
    }

    @Singleton
    @Provides
    fun provideMeasurementLocalDataSource(@ApplicationContext context: Context): MeasurementLocalDataSource {
        return MeasurementLocalDataSourceImpl(context)
    }

    @Singleton
    @Provides
    fun provideProductDetailRemoteDataSource(productApi: ProductApi): ProductDetailRemoteDataSource {
        return ProductDetailRemoteDataSourceImpl(productApi)
    }

    @Singleton
    @Provides
    fun provideProductSummaryRemoteDataSource(productApi: ProductApi): ProductSummaryRemoteDataSource {
        return ProductSummaryRemoteDataSourceImpl(productApi)
    }

    @Singleton
    @Provides
    fun provideProductApi(@ApplicationContext context: Context): ProductApi {
        return Retrofit.Builder()
            .baseUrl(context.getString(R.string.api_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .client(OkHttpClient())
            .build()
            .create(ProductApi::class.java)
    }
}