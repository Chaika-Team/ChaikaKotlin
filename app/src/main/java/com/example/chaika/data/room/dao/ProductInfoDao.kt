package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.chaika.data.room.entities.ProductInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductInfoDao {

    @Query("SELECT * FROM product_info")
    fun getAllProducts(): Flow<List<ProductInfo>>

    @Query("SELECT * FROM product_info WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): ProductInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductInfo)

    @Update
    suspend fun updateProduct(product: ProductInfo)

    @Delete
    suspend fun deleteProduct(product: ProductInfo)
}
