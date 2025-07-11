package com.example.chaika.domain.usecases

import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.OperationTypeDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Юзкейс добавления товара */

class AddOpUseCase @Inject constructor(
    private val saveOpUseCase: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(conductorId: Int) {
        val opDomain = CartOperationDomain(OperationTypeDomain.ADD, conductorId)
        saveOpUseCase(opDomain)
    }
}

class SoldCashOpUseCase @Inject constructor(
    private val saveOpUseCase: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(conductorId: Int) {
        val opDomain = CartOperationDomain(OperationTypeDomain.SOLD_CASH, conductorId)
        saveOpUseCase(opDomain)
    }
}

class SoldCardOpUseCase @Inject constructor(
    private val saveOpUseCase: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(conductorId: Int) {
        val opDomain = CartOperationDomain(OperationTypeDomain.SOLD_CART, conductorId)
        saveOpUseCase(opDomain)
    }
}

class ReplenishUseCase @Inject constructor(
    private val saveOpUseCase: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(conductorId: Int) {
        val opDomain = CartOperationDomain(OperationTypeDomain.REPLENISH, conductorId)
        saveOpUseCase(opDomain)
    }
}

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

/** Юзкейс изменения количества товара в корзине для операций ADD и REPLENISH, безлимитный */

class UpdateQuantityUnlimitedUseCase @Inject constructor(
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface
) {
    /** Всегда передаём большой лимит */
    operator fun invoke(itemId: Int, newQuantity: Int): Boolean =
        inMemoryCartRepository.updateItemQuantity(itemId, newQuantity, Int.MAX_VALUE)
}

/** Юзкейс изменения количества товара в корзине для операций SOLD_CART и SOLD_CASH, базируется на количестве товара в пакете */
class UpdateQuantityWithLimitUseCase @Inject constructor(
    private val inMemoryCartRepository: InMemoryCartRepositoryInterface,
    private val getAvailableQuantityUseCase: GetAvailableQuantityUseCase
) {
    suspend operator fun invoke(itemId: Int, newQuantity: Int): Boolean {
        // 1. Узнаём остаток
        val available = getAvailableQuantityUseCase(itemId)
        // 2. Пытаемся обновить
        return inMemoryCartRepository.updateItemQuantity(itemId, newQuantity, available)
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
