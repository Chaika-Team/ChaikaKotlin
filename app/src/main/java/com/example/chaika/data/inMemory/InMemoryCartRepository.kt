package com.example.chaika.data.inMemory

import com.example.chaika.data.CartRepository
import com.example.chaika.domain.models.Cart
import com.example.chaika.domain.models.CartItem

class InMemoryCartRepository : CartRepository {
    private val cart = Cart(mutableListOf())

    override fun addItem(item: CartItem) {
        cart.items.add(item)
    }

    override fun removeItem(itemId: Int) {
        cart.items.removeAll { it.product.id == itemId }
    }

    override fun getCartItems(): List<CartItem> {
        return cart.items
    }

    override fun clearCart() {
        cart.items.clear()
    }
}

