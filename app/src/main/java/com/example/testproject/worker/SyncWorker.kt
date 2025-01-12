package com.example.testproject.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.testproject.data.local.AppDatabase
import com.example.testproject.data.repository.ProductRepository
import com.example.testproject.utils.NetworkUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val database: AppDatabase by inject()
    private val repository: ProductRepository by inject()

    override suspend fun doWork(): Result {
        if (!NetworkUtils.isNetworkAvailable(applicationContext)) {
            return Result.retry()
        }

        try {
            val pendingProducts = database.productDao().getPendingUploads()

            pendingProducts.forEach { product ->
                // TODO: Implement the upload logic for pending products
                // This will be similar to your repository's addProduct function
                // but will need to handle the local image file
            }

            return Result.success()
        } catch (e: Exception) {
            return Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "SyncWorker"
    }
}