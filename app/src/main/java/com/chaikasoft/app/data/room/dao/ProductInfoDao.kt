package com.chaikasoft.app.data.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.chaikasoft.app.data.room.entities.ProductInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductInfoDao {

    @Query("SELECT * FROM product_info")
    fun getAllProducts(): Flow<List<ProductInfo>>

    @Query("SELECT * FROM product_info ORDER BY id ASC")
    fun getPagedProducts(): PagingSource<Int, ProductInfo>

    @Query("""
    SELECT * FROM product_info
    WHERE name COLLATE NOCASE LIKE :query ESCAPE '\'
    ORDER BY name ASC, id ASC
""")
    fun getPagedProductsByName(query: String): PagingSource<Int, ProductInfo>


    @Query("SELECT * FROM product_info WHERE id = :productId LIMIT 1")
    suspend fun getProductById(productId: Int): ProductInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductInfo)

    @Upsert
    suspend fun upsertProduct(product: ProductInfo)


    @Update
    suspend fun updateProduct(product: ProductInfo)

    @Delete
    suspend fun deleteProduct(product: ProductInfo)
}
