package com.example.testproject.data.repository

import android.content.Context
import com.example.testproject.data.local.dao.ProductDao
import com.example.testproject.data.local.entity.ProductEntity
import com.example.testproject.data.remote.ProductApi
import com.example.testproject.domain.model.Product
import com.example.testproject.utils.NetworkUtils
import com.example.testproject.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductRepository(
    private val api: ProductApi,
    private val dao: ProductDao,
    private val context: Context
) {
    fun getProducts(): Flow<Resource<List<Product>>> = flow {
        emit(Resource.Loading())
        try {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Online - fetch from API
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

                    // Save to local DB
                    dao.insertProducts(products.map { it.toEntity() })
                    emit(Resource.Success(products))
                } else {
                    // API error - fall back to local data
                    val localProducts = dao.getAllProducts()
                    emit(Resource.Success(localProducts.map { it.toDomain() }))
                }
            } else {
                // Offline - get from local DB
                val localProducts = dao.getAllProducts()
                emit(Resource.Success(localProducts.map { it.toDomain() }))
            }
        } catch (e: Exception) {
            // Error - try to get from local DB
            try {
                val localProducts = dao.getAllProducts()
                emit(Resource.Success(localProducts.map { it.toDomain() }))
            } catch (e2: Exception) {
                emit(Resource.Error(e2.message ?: "An error occurred"))
            }
        }
    }

    fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageFile: File?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            if (NetworkUtils.isNetworkAvailable(context)) {
                // Online - Add to server
                val productNameBody = productName.toRequestBody("text/plain".toMediaTypeOrNull())
                val productTypeBody = productType.toRequestBody("text/plain".toMediaTypeOrNull())
                val priceBody = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val taxBody = tax.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageFile?.let { file ->
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
                    // Save to local DB
                    dao.insertProduct(
                        ProductEntity(
                            productName = productName,
                            productType = productType,
                            price = price,
                            tax = tax,
                            imageUrl = response.body()?.productDetails?.image,
                            pendingUpload = false
                        )
                    )
                    emit(Resource.Success(Unit))
                } else {
                    emit(Resource.Error("Failed to add product"))
                }
            } else {
                // Offline - Save locally with pending flag
                val id = dao.insertProduct(
                    ProductEntity(
                        productName = productName,
                        productType = productType,
                        price = price,
                        tax = tax,
                        imageUrl = null,
                        pendingUpload = true,
                        localImagePath = imageFile?.absolutePath
                    )
                )
                if (id > 0) {
                    emit(Resource.Success(Unit))
                } else {
                    emit(Resource.Error("Failed to save product locally"))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    private fun Product.toEntity() = ProductEntity(
        productName = productName,
        productType = productType,
        price = price,
        tax = tax,
        imageUrl = imageUrl,
        pendingUpload = false
    )

    private fun ProductEntity.toDomain() = Product(
        id = id,
        productName = productName,
        productType = productType,
        price = price,
        tax = tax,
        imageUrl = imageUrl
    )
}