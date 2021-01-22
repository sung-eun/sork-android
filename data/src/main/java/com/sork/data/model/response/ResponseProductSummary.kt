package com.sork.data.model.response

import com.google.gson.annotations.SerializedName

data class ResponseProductSummary(
    @SerializedName("prd_idx") val id: String,
    @SerializedName("prd_img") val imageUrl: String?,
    @SerializedName("prd_brand") val brand: String?,
    @SerializedName("prd_name") val name: String?,
    @SerializedName("prd_price") val price: String?
)