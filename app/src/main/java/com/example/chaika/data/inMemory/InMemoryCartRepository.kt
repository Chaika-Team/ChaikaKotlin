package com.example.chaika.data.inMemory

import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import javax.inject.Inject

class InMemoryCartRepository @Inject constructor() : InMemoryCartRepositoryInterface {

    private val cart = CartDomain(mutableListOf())

    // Добавление товара в корзину
    override fun addItemToCart(item: CartItemDomain): Boolean {
        val existingItem = cart.items.find { it.product.id == item.product.id }
        return if (existingItem == null) {
            cart.items.add(item)
            true // Возвращаем true, если товар добавлен
        } else {
            false // Возвращаем false, если товар уже существует
        }
    }

    // Удаление товара из корзины
    override fun removeItemFromCart(itemId: Int): Boolean {
        val existingItem = cart.items.find { it.product.id == itemId }
        return if (existingItem != null) {
            cart.items.remove(existingItem)
            true // Возвращаем true, если товар удалён
        } else {
            false // Возвращаем false, если товара не было в корзине
        }
    }

    // Обновление количества товара
    override fun updateItemQuantity(
        itemId: Int,
        newQuantity: Int,
        availableQuantity: Int
    ): Boolean {
        val existingItem =
            cart.items.firstOrNull { it.product.id == itemId } // Используем firstOrNull для безопасности

        return if (existingItem != null && newQuantity <= availableQuantity) {
            existingItem.quantity = newQuantity
            true
        } else {
            false // Превышение доступного количества или товар не найден
        }
    }

    // Возвращаем список товаров в корзине
    override fun getCartItems(): MutableList<CartItemDomain> {
        return cart.items // Возвращаем копию списка
    }

    // Очищаем корзину после завершения операции
    override fun clearCart() {
        cart.items.clear()
    }

    // Возвращаем текущую корзину
    override fun getCart(): CartDomain {
        return cart
    }
}
