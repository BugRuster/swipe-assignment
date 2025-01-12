package com.example.testproject.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.repository.ProductRepository
import com.example.testproject.domain.model.Product
import com.example.testproject.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var allProducts = emptyList<Product>()
    private val _searchQuery = MutableStateFlow("")

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            repository.getProducts().collect { result ->
                _uiState.value = when (result) {
                    is Resource.Success -> {
                        allProducts = result.data
                        UiState.Success(result.data)
                    }
                    is Resource.Error -> UiState.Error(result.message)
                    is Resource.Loading -> UiState.Loading
                }
            }
        }
    }

    fun searchProducts(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _uiState.value = UiState.Success(allProducts)
            return
        }

        val filteredProducts = allProducts.filter {
            it.productName.contains(query, ignoreCase = true) ||
                    it.productType.contains(query, ignoreCase = true)
        }
        _uiState.value = UiState.Success(filteredProducts)
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val products: List<Product>) : UiState()
        data class Error(val message: String) : UiState()
    }
}