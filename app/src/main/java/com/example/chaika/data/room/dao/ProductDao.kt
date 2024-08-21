package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chaika.models.ProductInTrip
import com.example.chaika.data.room.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Query("SELECT * FROM products ORDER BY title ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int

    @Query("""
    SELECT products.id, products.title, products.price,
        SUM(CASE WHEN actions.operation_id = 1 THEN actions.count ELSE 0 END) as added,
        SUM(CASE WHEN actions.operation_id = 2 THEN actions.count ELSE 0 END) as boughtCash,
        SUM(CASE WHEN actions.operation_id = 3 THEN actions.count ELSE 0 END) as boughtCard,
        SUM(CASE WHEN actions.operation_id = 4 THEN actions.count ELSE 0 END) as replenished
    FROM products
    INNER JOIN actions ON products.id = actions.product_id
    WHERE actions.trip_id = :tripId
    GROUP BY products.id, products.title, products.price
""")
    suspend fun getProductsByTrip(tripId: Int): List<ProductInTrip>

}
