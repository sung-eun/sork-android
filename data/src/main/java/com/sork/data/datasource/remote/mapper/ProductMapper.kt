package com.sork.data.datasource.remote.mapper

import com.sork.data.model.response.ResponseProduct
import com.sork.data.model.response.ResponseProductSummaries
import com.sork.data.model.response.ResponseProductSummary
import com.sork.domain.entity.DetailSize
import com.sork.domain.entity.MeasurementType
import com.sork.domain.entity.Product
import com.sork.domain.entity.ProductSummary
import io.reactivex.rxjava3.core.Observable
import java.text.NumberFormat
import java.util.*

object ProductMapper {
    fun mapToProductSummaries(response: ResponseProductSummaries): List<ProductSummary> {
        if (response.total == 0 || response.list.isNullOrEmpty()) {
            return emptyList()
        }

        return Observable.fromIterable(response.list)
            .map { mapToProductSummary(it) }
            .toList()
            .blockingGet()
    }

    private fun mapToProductSummary(response: ResponseProductSummary): ProductSummary {
        return ProductSummary(response.id, response.imageUrl ?: "", response.brand ?: "", response.name ?: "", getFormattedPrice(response.price))
    }

    fun mapToProduct(id: String, response: ResponseProduct): Product {
        return Product(
            id,
            response.imageUrl ?: "",
            response.brand ?: "",
            response.name ?: "",
            getFormattedPrice(response.price),
            response.url ?: "",
            response.matchedSize ?: "",
            getDetailSizes(response)
        )
    }

    private fun getFormattedPrice(price: String?): String {
        if (price.isNullOrEmpty()) return ""
        return try {
            NumberFormat.getNumberInstance(Locale.US).format(price.toInt())
        } catch (e: NumberFormatException) {
            price
        }
    }

    private fun getDetailSizes(response: ResponseProduct): List<DetailSize> {
        return listOf(
            DetailSize(MeasurementType.SHOULDER_WIDTH, stringToDouble(response.shoulder)),
            DetailSize(MeasurementType.SLEEVE_LENGTH, stringToDouble(response.arm)),
            DetailSize(MeasurementType.BUST_WIDTH, stringToDouble(response.chest)),
            DetailSize(MeasurementType.TOTAL_LENGTH, stringToDouble(response.length))
        )
    }

    private fun stringToDouble(value: String?): Double {
        if (value == null) {
            return 0.0
        }
        return try {
            value.toDouble()
        } catch (e: java.lang.NumberFormatException) {
            0.0
        }
    }
}