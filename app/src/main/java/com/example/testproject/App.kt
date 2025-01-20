package com.example.testproject

import android.app.Application
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.testproject.di.databaseModule
import com.example.testproject.di.networkModule
import com.example.testproject.utils.ConnectivityObserver
import com.example.testproject.worker.SyncWorker
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class App : Application() {

    private val workManager: WorkManager by inject()

    override fun onCreate() {
        super.onCreate()

        setupKoin()
        setupWorkManager()
        observeConnectivity()
    }

    private fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(networkModule, databaseModule)
        }
    }

// App.kt
private fun setupWorkManager() {
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
        SyncWorker.SYNC_INTERVAL, TimeUnit.MINUTES
    )
        .setConstraints(constraints)
        .build()

    workManager.enqueueUniquePeriodicWork(
        SyncWorker.WORK_NAME,
        ExistingPeriodicWorkPolicy.REPLACE, // Change to REPLACE
        syncRequest
    )
}

    private fun observeConnectivity() {
        val connectivityObserver = ConnectivityObserver(this)

        connectivityObserver.observe().onEach { status ->
            when (status) {
                ConnectivityObserver.Status.Available -> {
                    // Trigger immediate sync when connection becomes available
                    val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                        15, TimeUnit.MINUTES
                    ).build()

                    workManager.enqueue(syncRequest)
                }
                else -> { /* Handle other states if needed */ }
            }
        }
    }
}