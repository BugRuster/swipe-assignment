package com.example.testproject.data.repository

import android.content.Context
import com.example.testproject.data.local.dao.ProductDao
import com.example.testproject.data.local.entity.ProductEntity
import com.example.testproject.data.remote.ProductApi
import com.example.testproject.domain.model.Product
import com.example.testproject.utils.NetworkUtils
import com.example.testproject.utils.Resource
import com.example.testproject.utils.networkBoundResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

// ProductRepository.kt
class ProductRepository(
    private val api: ProductApi,
    private val dao: ProductDao,
    private val context: Context
) {
    suspend fun getProducts(): Flow<Resource<List<Product>>> = networkBoundResource(
        query = {
            dao.getAllProducts().map { it.toDomain() }
        },
        fetch = {
            api.getProducts().body()?.map { response ->
                Product(
                    productName = response.productName,
                    productType = response.productType,
                    price = response.price,
                    tax = response.tax,
                    imageUrl = response.image
                )
            } ?: emptyList()
        },
        saveFetchResult = { remoteProducts ->
            // Get all pending products
            val pendingProducts = dao.getPendingUploads()
            
            // Get current max display order
            val maxOrder = dao.getMaxDisplayOrder() ?: System.currentTimeMillis()
            
            // Clear cached products
            dao.clearCachedProducts()
            
            // Convert remote products to entities with display order
    // ProductRepository.kt
// In the saveFetchResult lambda, fix the tax reference:

val remoteEntities = remoteProducts.mapIndexed { index, product ->
    ProductEntity(
        productName = product.productName,
        productType = product.productType,
        price = product.price,
        tax = product.tax,  // Changed from tax to product.tax
        imageUrl = product.imageUrl,
        pendingUpload = false,
        createdAt = System.currentTimeMillis(),
        displayOrder = maxOrder - ((1000 + index) * 1000L)
    )
}
            
            // Keep pending products as they are
            dao.insertProducts(pendingProducts + remoteEntities)
        },
        shouldFetch = {
            NetworkUtils.isNetworkAvailable(context)
        }
    )

    suspend fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageFile: File?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val currentTime = System.currentTimeMillis()
            val maxOrder = (dao.getMaxDisplayOrder() ?: currentTime) + 1000L

            if (NetworkUtils.isNetworkAvailable(context)) {
                // Online flow
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
                    dao.insertProduct(
                        ProductEntity(
                            productName = productName,
                            productType = productType,
                            price = price,
                            tax = tax,
                            imageUrl = response.body()?.productDetails?.image,
                            pendingUpload = false,
                            createdAt = currentTime,
                            displayOrder = maxOrder
                        )
                    )
                    emit(Resource.Success(Unit))
                } else {
                    emit(Resource.Error("Failed to add product"))
                }
            } else {
                // Offline flow
                dao.insertProduct(
                    ProductEntity(
                        productName = productName,
                        productType = productType,
                        price = price,
                        tax = tax,
                        imageUrl = null,
                        pendingUpload = true,
                        localImagePath = imageFile?.absolutePath,
                        createdAt = currentTime,
                        displayOrder = maxOrder
                    )
                )
                emit(Resource.Success(Unit))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    private fun ProductEntity.toDomain() = Product(
        id = id,
        productName = productName,
        productType = productType,
        price = price,
        tax = tax,
        imageUrl = if (pendingUpload) localImagePath else imageUrl
    )
}