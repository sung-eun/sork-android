package com.sork.data.datasource.remote.api

import com.sork.data.model.response.ResponseProduct
import com.sork.data.model.response.ResponseProductSummaries
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ProductApi {
    @GET("list")
    fun getList(): Single<ResponseProductSummaries>

    @GET("list")
    fun getList(@QueryMap queryMap: Map<String, String>): Single<ResponseProductSummaries>

    @GET("detail/{id}")
    fun getProduct(@Path("id") id: String): Single<ResponseProduct>
}