package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.chaika.data.room.entities.PackageItemView
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageItemViewDao {

    // Получение всех товаров у проводника с использованием Flow
    @Query("SELECT * FROM package_items")
    fun getPackageItems(): Flow<List<PackageItemView>>

    // Получение конкретного товара по его ID
    @Query("SELECT * FROM package_items WHERE product_id = :productId")
    suspend fun getPackageItemByProductId(productId: Int): PackageItemView?
}
