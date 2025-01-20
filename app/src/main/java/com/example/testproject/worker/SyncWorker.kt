package com.example.testproject.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.testproject.data.local.AppDatabase
import com.example.testproject.data.remote.ProductApi
import com.example.testproject.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File

// SyncWorker.kt
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val database: AppDatabase by inject()
    private val api: ProductApi by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (!NetworkUtils.isNetworkAvailable(applicationContext)) {
                return@withContext Result.retry()
            }

            val pendingProducts = database.productDao().getPendingUploads()
            
            if (pendingProducts.isEmpty()) {
                return@withContext Result.success()
            }

            var hasError = false
            
            pendingProducts.forEach { product ->
                try {
                    val productNameBody = product.productName.toRequestBody("text/plain".toMediaTypeOrNull())
                    val productTypeBody = product.productType.toRequestBody("text/plain".toMediaTypeOrNull())
                    val priceBody = product.price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val taxBody = product.tax.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                    val imagePart = product.localImagePath?.let { path ->
                        val file = File(path)
                        if (file.exists()) {
                            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                            MultipartBody.Part.createFormData("files[]", file.name, requestFile)
                        } else null
                    }

                    val response = api.addProduct(
                        productName = productNameBody,
                        productType = productTypeBody,
                        price = priceBody,
                        tax = taxBody,
                        file = imagePart
                    )

                    if (response.isSuccessful && response.body()?.success == true) {
                        // Update the product with new image URL from server
                        database.productDao().updateUploadedProduct(
                            id = product.id,
                            imageUrl = response.body()?.productDetails?.image
                        )

                        // Clean up local image file if it exists
                        product.localImagePath?.let { path ->
                            try {
                                val file = File(path)
                                if (file.exists()) {
                                    file.delete()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        hasError = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    hasError = true
                }
            }

            if (hasError) Result.retry() else Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "SyncWorker"
        const val SYNC_INTERVAL = 5L // in minutes
    }
}