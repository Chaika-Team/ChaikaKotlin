package com.example.chaika.domain.usecases

import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import javax.inject.Inject

// Сохранение операций и очистка корзины
class SaveCartWithItemsAndOperationUseCase @Inject constructor(
    private val roomCartRepositoryInterface: RoomCartRepositoryInterface, // Репозиторий для работы с базой данных
    private val inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface, // Репозиторий для работы с корзиной в памяти
) {
    suspend operator fun invoke(cartOperationDomain: CartOperationDomain) {
        // Получаем текущее состояние корзины
        val cart = CartDomain(inMemoryCartRepositoryInterface.getCartItems())

        // Сохраняем корзину и операцию в базе данных
        roomCartRepositoryInterface.saveCartWithItemsAndOperation(cart, cartOperationDomain)

        // Очищаем корзину в оперативной памяти после завершения операции
        inMemoryCartRepositoryInterface.clearCart()
    }
}

// Добавление товара в корзину
class AddItemToCartUseCase @Inject constructor(
    private val inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(item: CartItemDomain): Boolean {
        return inMemoryCartRepositoryInterface.addItemToCart(item)
    }
}

// Удаление товара из корзины
class RemoveItemFromCartUseCase @Inject constructor(
    private val inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(itemId: Int): Boolean {
        return inMemoryCartRepositoryInterface.removeItemFromCart(itemId)
    }
}

// Изменение количества товара в корзине
class UpdateItemQuantityInCartUseCase @Inject constructor(
    private val inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(itemId: Int, newQuantity: Int, availableQuantity: Int): Boolean {
        // Обновляем количество товара в корзине, если оно не превышает доступное
        return inMemoryCartRepositoryInterface.updateItemQuantity(
            itemId,
            newQuantity,
            availableQuantity,
        )
    }
}

// Получение товаров из корзины.
class GetCartItemsUseCase @Inject constructor(
    private val inMemoryCartRepositoryInterface: InMemoryCartRepositoryInterface,
) {
    operator fun invoke(): List<CartItemDomain> {
        // Возвращаем список товаров в корзине
        return inMemoryCartRepositoryInterface.getCartItems()
    }
}
