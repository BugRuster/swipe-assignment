package com.example.testproject.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ProductEntity.kt
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val imageUrl: String?,
    val pendingUpload: Boolean = false,
    val localImagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val displayOrder: Long = createdAt // New field for display ordering
)