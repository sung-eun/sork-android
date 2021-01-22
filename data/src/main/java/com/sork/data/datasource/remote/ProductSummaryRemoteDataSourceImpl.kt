package com.sork.data.datasource.remote

import com.sork.common.util.MeasurementUtil
import com.sork.data.datasource.remote.api.ProductApi
import com.sork.data.datasource.remote.mapper.ProductMapper
import com.sork.domain.entity.Measurement
import com.sork.domain.entity.MeasurementType
import com.sork.domain.entity.ProductSummary
import io.reactivex.rxjava3.core.Single

class ProductSummaryRemoteDataSourceImpl(private val api: ProductApi) : ProductSummaryRemoteDataSource {
    override fun getProductSummaries(): Single<List<ProductSummary>> {
        return api.getList()
            .map { ProductMapper.mapToProductSummaries(it) }
    }

    override fun getProductSummaries(measurements: List<Measurement>): Single<List<ProductSummary>> {
        return api.getList(buildQueryMap(measurements))
            .map { ProductMapper.mapToProductSummaries(it) }
    }

    private fun buildQueryMap(measurements: List<Measurement>): Map<String, String> {
        val queryMap = HashMap<String, String>()
        measurements.forEach {
            if (it.selected) {
                val key = when (it.type) {
                    MeasurementType.SHOULDER_WIDTH -> "shoulder"
                    MeasurementType.SLEEVE_LENGTH -> "arm"
                    MeasurementType.BUST_WIDTH -> "chest"
                    MeasurementType.TOTAL_LENGTH -> "length"
                }
                queryMap[key] = MeasurementUtil.getAdjustedValue(it.value, true)
            }
        }
        return queryMap
    }

}