package com.example.chaika.data.room.dao

import androidx.room.*
import com.example.chaika.data.room.entities.CartOperation

@Dao
interface CartOperationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(cartOperation: CartOperation): Long

    @Query("SELECT * FROM cart_operations WHERE id = :id")
    suspend fun getById(id: Int): CartOperation?

    @Delete
    suspend fun delete(cartOperation: CartOperation)
}
