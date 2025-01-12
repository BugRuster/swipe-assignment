package com.example.testproject.data.repository

import android.net.Uri
import com.example.testproject.data.remote.ProductApi
import com.example.testproject.domain.model.Product
import com.example.testproject.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductRepository(
    private val api: ProductApi
) {
    fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                val products = response.body()?.map { productResponse ->
                    Product(
                        productName = productResponse.productName,
                        productType = productResponse.productType,
                        price = productResponse.price,
                        tax = productResponse.tax,
                        imageUrl = productResponse.image
                    )
                } ?: emptyList()
                emit(Resource.Success(products))
            } else {
                emit(Resource.Error("Failed to fetch products"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageUri: Uri?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val productNameBody = productName.toRequestBody("text/plain".toMediaTypeOrNull())
            val productTypeBody = productType.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val taxBody = tax.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageUri?.let { uri ->
                val file = File(uri.path!!)
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("files[]", file.name, requestFile)
            }

            val response = api.addProduct(
                productName = productNameBody,
                productType = productTypeBody,
                price = priceBody,
                tax = taxBody,
                file = imagePart
            )

            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Failed to add product"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }
}