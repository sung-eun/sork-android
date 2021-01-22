package com.sork.data.datasource.remote.mapper

import com.sork.data.model.response.ResponseProductSummaries
import com.sork.data.model.response.ResponseProductSummary
import com.sork.domain.entity.ProductSummary
import io.reactivex.rxjava3.core.Observable
import java.text.NumberFormat
import java.util.*

object ProductSummaryMapper {
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

    private fun getFormattedPrice(price: String?): String {
        if (price.isNullOrEmpty()) return ""
        return try {
            NumberFormat.getNumberInstance(Locale.US).format(price.toInt())
        } catch (e: NumberFormatException) {
            price
        }
    }
}