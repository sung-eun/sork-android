package com.sork.data.model.response

import com.google.gson.annotations.SerializedName

data class ResponseProductSummaries(
    @SerializedName("total") val total: Int,
    @SerializedName("list") val list: List<ResponseProductSummary>?
)