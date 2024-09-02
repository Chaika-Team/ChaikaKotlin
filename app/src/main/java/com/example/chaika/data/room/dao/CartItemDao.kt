package com.example.chaika.data.room.dao

import androidx.room.*
import com.example.chaika.data.room.entities.CartItem

@Dao
interface CartItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem): Long

    @Query("SELECT * FROM cart_items WHERE cart_operation_id = :cartOperationId")
    suspend fun getCartItemsByCartOpId(cartOperationId: Int): List<CartItem>

    @Delete
    suspend fun delete(cartItem: CartItem)
}
