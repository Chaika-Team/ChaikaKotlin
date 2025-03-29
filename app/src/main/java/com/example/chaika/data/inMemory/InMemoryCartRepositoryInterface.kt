package com.example.chaika.data.inMemory

import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import kotlinx.coroutines.flow.Flow

interface InMemoryCartRepositoryInterface {
    fun addItemToCart(item: CartItemDomain): Boolean
    fun removeItemFromCart(itemId: Int): Boolean
    fun updateItemQuantity(itemId: Int, newQuantity: Int, availableQuantity: Int): Boolean
    fun getCartItems(): Flow<List<CartItemDomain>>  // Изменено: возвращаем Flow с неизменяемой копией списка
    fun clearCart()
    fun getCart(): CartDomain
}
