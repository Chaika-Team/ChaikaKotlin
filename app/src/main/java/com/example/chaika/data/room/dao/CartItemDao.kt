package com.example.chaika.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chaika.data.room.entities.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem): Long

    @Query("SELECT * FROM cart_items WHERE cart_operation_id = :cartOperationId")
    fun getCartItemsByCartOpId(cartOperationId: Int): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Delete
    suspend fun delete(cartItem: CartItem)
}
