package com.chaikasoft.app.data.inMemory

import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import kotlinx.coroutines.flow.Flow

interface InMemoryCartRepositoryInterface {
    fun addItemToCart(item: CartItemDomain): Boolean
    fun removeItemFromCart(itemId: Int): Boolean
    fun updateItemQuantity(itemId: Int, newQuantity: Int, availableQuantity: Int): Boolean
    fun getCartItems(): Flow<List<CartItemDomain>>  // Изменено: возвращаем Flow с неизменяемой копией списка
    fun clearCart()
    fun getCart(): CartDomain
}
