package com.example.chaika.domain.usecases

import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

// Сохранение операций и очистка корзины
class SaveCartWithItemsAndOperationUseCase @Inject constructor(
    private val roomCartRepository: RoomCartRepositoryInterface,
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface
) {
    suspend operator fun invoke(cartOperationDomain: CartOperationDomain) {
        // Получаем текущий список товаров из in-memory корзины через Flow
        val cart = CartDomain(inMemoryCartRepository.getCartItems().first().toMutableList())
        // Сохраняем корзину и операцию в базе данных
        roomCartRepository.saveCartWithItemsAndOperation(cart, cartOperationDomain)
        // Очищаем in-memory корзину после сохранения
        inMemoryCartRepository.clearCart()
    }
}

// Добавление товара в корзину
class AddItemToCartUseCase @Inject constructor(
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(item: CartItemDomain): Boolean {
        return inMemoryCartRepository.addItemToCart(item)
    }
}

// Удаление товара из корзины
class RemoveItemFromCartUseCase @Inject constructor(
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(itemId: Int): Boolean {
        return inMemoryCartRepository.removeItemFromCart(itemId)
    }
}

// Изменение количества товара в корзине
class UpdateItemQuantityInCartUseCase @Inject constructor(
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(itemId: Int, newQuantity: Int, availableQuantity: Int): Boolean {
        // Обновляем количество товара в корзине, если оно не превышает доступное
        return inMemoryCartRepository.updateItemQuantity(
            itemId,
            newQuantity,
            availableQuantity,
        )
    }
}

// Получение товаров из корзины.
class GetCartItemsUseCase @Inject constructor(
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(): Flow<List<CartItemDomain>> {
        // Возвращаем список товаров в корзине
        return inMemoryCartRepository.getCartItems()
    }
}
