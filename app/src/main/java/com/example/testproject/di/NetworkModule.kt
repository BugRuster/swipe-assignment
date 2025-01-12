package com.example.testproject.di

import com.example.testproject.data.remote.ProductApi
import com.example.testproject.data.repository.ProductRepository
import com.example.testproject.presentation.addproduct.viewmodel.AddProductViewModel
import com.example.testproject.presentation.home.viewmodel.HomeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://app.getswipe.in/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(ProductApi::class.java)
    }

    single {
        ProductRepository(get())
    }

    // ViewModels
    viewModel {
        HomeViewModel(get())
    }

    viewModel {
        AddProductViewModel(get())
    }
}