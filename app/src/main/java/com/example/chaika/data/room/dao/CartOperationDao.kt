package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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