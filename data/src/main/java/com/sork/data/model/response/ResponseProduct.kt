package com.sork.data.model.response

import com.google.gson.annotations.SerializedName

data class ResponseProduct(
    @SerializedName("prd_img") val imageUrl: String?,
    @SerializedName("prd_brand") val brand: String?,
    @SerializedName("prd_name") val name: String?,
    @SerializedName("prd_price") val price: String?,
    @SerializedName("prd_url") val url: String?,
    @SerializedName("prd_size") val matchedSize: String?,
    @SerializedName("shoulder") val shoulder: String?,
    @SerializedName("arm") val arm: String?,
    @SerializedName("chest") val chest: String?,
    @SerializedName("length") val length: String?,
)