package com.example.testproject.data.model

import com.google.gson.annotations.SerializedName

data class AddProductResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("product_details")
    val productDetails: ProductResponse,
    @SerializedName("product_id")
    val productId: Int,
    @SerializedName("success")
    val success: Boolean
)
