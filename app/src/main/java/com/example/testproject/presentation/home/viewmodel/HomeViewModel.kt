// HomeViewModel.kt
package com.example.testproject.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testproject.data.repository.ProductRepository
import com.example.testproject.domain.model.Product
import com.example.testproject.utils.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private var searchJob: Job? = null

    init {
        fetchProducts()
    }

// HomeViewModel.kt (continued)
fun fetchProducts() {
        viewModelScope.launch {
            repository.getProducts().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        allProducts = result.data
                        // Re-apply current search query
                        if (_searchQuery.value.isNotEmpty()) {
                            searchProducts(_searchQuery.value)
                        } else {
                            _uiState.value = UiState.Success(result.data)
                        }
                    }
                    is Resource.Error -> _uiState.value = UiState.Error(result.message)
                    is Resource.Loading -> _uiState.value = UiState.Loading
                }
            }
        }
    }

    fun searchProducts(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchQuery.value = query
            delay(300) // Debounce delay
            
            if (query.isEmpty()) {
                _uiState.value = UiState.Success(allProducts)
                return@launch
            }

            val normalizedQuery = query.trim().lowercase()
            val filteredProducts = allProducts.filter {
                it.productName.lowercase().contains(normalizedQuery) ||
                        it.productType.lowercase().contains(normalizedQuery) ||
                        it.price.toString().contains(normalizedQuery)
            }
            _uiState.value = UiState.Success(filteredProducts)
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val products: List<Product>) : UiState()
        data class Error(val message: String) : UiState()
    }
}