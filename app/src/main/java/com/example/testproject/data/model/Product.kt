package com.example.testproject.domain.model

data class Product(
    val id: Int = 0,
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val imageUrl: String? = null
)