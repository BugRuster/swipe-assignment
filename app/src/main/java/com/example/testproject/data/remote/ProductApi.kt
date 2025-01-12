package com.example.testproject.data.remote

import com.example.testproject.data.model.AddProductResponse
import com.example.testproject.data.model.ProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProductApi {
    @GET("api/public/get")
    suspend fun getProducts(): Response<List<ProductResponse>>

    @Multipart
    @POST("api/public/add")
    suspend fun addProduct(
        @Part("product_name") productName: RequestBody,
        @Part("product_type") productType: RequestBody,
        @Part("price") price: RequestBody,
        @Part("tax") tax: RequestBody,
        @Part file: MultipartBody.Part?
    ): Response<AddProductResponse>
}