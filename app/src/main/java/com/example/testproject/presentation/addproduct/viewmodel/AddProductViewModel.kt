package com.example.testproject.presentation.addproduct.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.repository.ProductRepository
import com.example.testproject.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var selectedImageUri: Uri? = null

    fun setSelectedImage(uri: Uri) {
        selectedImageUri = uri
    }

    fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double
    ) {
        viewModelScope.launch {
            repository.addProduct(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                imageUri = selectedImageUri
            ).collect { result ->
                _uiState.value = when (result) {
                    is Resource.Success -> UiState.Success
                    is Resource.Error -> UiState.Error(result.message)
                    is Resource.Loading -> UiState.Loading
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = UiState.Initial
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}