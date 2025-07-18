package com.example.chaika.data.inMemory

import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class InMemoryCartRepository @Inject constructor() : InMemoryCartRepositoryInterface {

    // Используем MutableStateFlow для хранения списка элементов корзины
    private val _cartItems = MutableStateFlow<List<CartItemDomain>>(emptyList())

    // Возвращаем Flow неизменяемого списка
    override fun getCartItems(): Flow<List<CartItemDomain>> = _cartItems.asStateFlow()

    override fun addItemToCart(item: CartItemDomain): Boolean {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.product.id == item.product.id }
        return if (existingItem == null) {
            currentItems.add(item)
            _cartItems.value = currentItems.toList() // Возвращаем неизменяемую копию
            true
        } else {
            false
        }
    }

    override fun removeItemFromCart(itemId: Int): Boolean {
        val currentItems = _cartItems.value.toMutableList()
        val removed = currentItems.removeAll { it.product.id == itemId }
        if (removed) {
            _cartItems.value = currentItems.toList()
        }
        return removed
    }

    override fun updateItemQuantity(
        itemId: Int,
        newQuantity: Int,
        availableQuantity: Int
    ): Boolean {
        if (newQuantity > availableQuantity) return false

        val currentItems = _cartItems.value.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == itemId }
        if (index != -1) {
            currentItems[index] = currentItems[index].copy(quantity = newQuantity)
            _cartItems.value = currentItems.toList()
            return true
        }
        return false
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
    }

    override fun getCart(): CartDomain {
        // Возвращаем новую корзину с копией текущих элементов
        return CartDomain(_cartItems.value.toMutableList())
    }
}
