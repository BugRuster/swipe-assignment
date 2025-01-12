package com.example.testproject.presentation.shared

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SharedViewModel : ViewModel() {
    private val _refreshProducts = MutableSharedFlow<Unit>()
    val refreshProducts = _refreshProducts.asSharedFlow()

    suspend fun refreshProducts() {
        _refreshProducts.emit(Unit)
    }
}