package com.example.testproject.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testproject.data.local.entity.ProductEntity

// ProductDao.kt
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY displayOrder DESC")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE pendingUpload = 1 ORDER BY displayOrder DESC")
    suspend fun getPendingUploads(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Query("UPDATE products SET pendingUpload = 0, imageUrl = :imageUrl WHERE id = :id")
    suspend fun updateUploadedProduct(id: Int, imageUrl: String?)

    @Query("DELETE FROM products WHERE pendingUpload = 0")
    suspend fun clearCachedProducts()

    @Query("SELECT MAX(displayOrder) FROM products")
    suspend fun getMaxDisplayOrder(): Long?
}