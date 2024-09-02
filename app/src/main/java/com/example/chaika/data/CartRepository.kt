package com.example.chaika.data

import com.example.chaika.domain.models.CartItem

interface CartRepository {
    fun addItem(item: CartItem)
    fun removeItem(itemId: Int)
    fun getCartItems(): List<CartItem>
    fun clearCart()
}