package com.example.testproject.di

import androidx.room.Room
import androidx.work.WorkManager
import com.example.testproject.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    single { get<AppDatabase>().productDao() }

    single { WorkManager.getInstance(androidContext()) }
}