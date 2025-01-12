package com.example.testproject.presentation.addproduct.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.repository.ProductRepository
import com.example.testproject.utils.FileUtils
import com.example.testproject.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AddProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var selectedImageFile: File? = null

    fun setSelectedImage(context: Context, uri: Uri) {
        try {
            selectedImageFile = FileUtils.getFile(context, uri)
        } catch (e: Exception) {
            _uiState.value = UiState.Error("Failed to process image: ${e.message}")
        }
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
                imageFile = selectedImageFile
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
        selectedImageFile = null
    }

    sealed class UiState {
        object Initial : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}