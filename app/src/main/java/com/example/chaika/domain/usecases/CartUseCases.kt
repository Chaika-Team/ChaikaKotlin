package com.example.chaika.domain.usecases

import com.example.chaika.data.inMemory.CartRepositoryFactoryInterface
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.data.room.repo.RoomCartRepositoryInterface
import com.example.chaika.domain.models.CartDomain
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.CartOperationDomain
import com.example.chaika.domain.models.OperationTypeDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject


/**
 * Use Case, отвечающий за выдачу новой in‑memory корзины.
 * Инкапсулирует фабрику и скрывает её от ViewModel.
 */
class CreateCartUseCase @Inject constructor(
    private val factory: CartRepositoryFactoryInterface
) {
    /**
     * Возвращает свежий экземпляр корзины.
     */
    operator fun invoke(): InMemoryCartRepositoryInterface = factory.create()
}

/**
 * Сохраняет список элементов из переданной корзины и операцию в БД,
 * затем очищает саму корзину.
 */
class SaveCartWithItemsAndOperationUseCase @Inject constructor(
    private val roomCartRepository: RoomCartRepositoryInterface
) {
    suspend operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        operation: CartOperationDomain
    ) {
        // 1) Получаем элементы из in‑memory корзины
        val items = cart.getCartItems().first()
        // 2) Сохраняем в БД
        roomCartRepository.saveCartWithItemsAndOperation(
            CartDomain(items.toMutableList()),
            operation
        )
        // 3) Очищаем in‑memory корзину
        cart.clearCart()
    }
}

/**
 * Operation‑use cases: обёртки над SaveCartWithItemsAndOperationUseCase
 * Каждая устанавливает свой тип операции, и вызывает общий saveUC.
 */
class AddOpUseCase @Inject constructor(
    private val saveOp: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        conductorId: Int
    ) {
        saveOp(cart, CartOperationDomain(OperationTypeDomain.ADD, conductorId))
    }
}

class SoldCashOpUseCase @Inject constructor(
    private val saveOp: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        conductorId: Int
    ) {
        saveOp(cart, CartOperationDomain(OperationTypeDomain.SOLD_CASH, conductorId))
    }
}

class SoldCardOpUseCase @Inject constructor(
    private val saveOp: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        conductorId: Int
    ) {
        saveOp(cart, CartOperationDomain(OperationTypeDomain.SOLD_CART, conductorId))
    }
}

class ReplenishUseCase @Inject constructor(
    private val saveOp: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        conductorId: Int
    ) {
        saveOp(cart, CartOperationDomain(OperationTypeDomain.REPLENISH, conductorId))
    }
}

/**
 * Добавление товара в корзину.
 */
class AddItemToCartUseCase @Inject constructor() {
    operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        item: CartItemDomain
    ): Boolean = cart.addItemToCart(item)
}

/**
 * Добавление товара в корзину с проверкой остатка (для SOLD_* сценариев).
 * Не добавляет товар, если доступный остаток == 0.
 */
class AddItemToCartWithLimitUseCase @Inject constructor(
    private val getAvailableQuantity: GetAvailableQuantityUseCase
) {
    suspend operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        item: CartItemDomain
    ): Boolean {
        val available = getAvailableQuantity(item.product.id)
        if (available <= 0) return false
        // Репозиторий сам выставляет quantity = 1 и не добавляет дубликаты
        return cart.addItemToCart(item)
    }
}

/**
 * Удаление товара из корзины.
 */
class RemoveItemFromCartUseCase @Inject constructor() {
    operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        itemId: Int
    ): Boolean = cart.removeItemFromCart(itemId)
}

/**
 * Обновление количества товара без учёта лимита (для ADD и REPLENISH).
 */
class UpdateQuantityUnlimitedUseCase @Inject constructor() {
    operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        itemId: Int,
        newQuantity: Int
    ): Boolean = cart.updateItemQuantity(itemId, newQuantity, Int.MAX_VALUE)
}

/**
 * Обновление количества товара с проверкой остатка (для SOLD_CASH и SOLD_CART).
 */
class UpdateQuantityWithLimitUseCase @Inject constructor(
    private val getAvailableQuantity: GetAvailableQuantityUseCase
) {
    suspend operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        itemId: Int,
        newQuantity: Int
    ): Boolean {
        val available = getAvailableQuantity(itemId)
        return cart.updateItemQuantity(itemId, newQuantity, available)
    }
}

/**
 * Явное обновление количества с передачей готового availableQuantity.
 */
class UpdateItemQuantityInCartUseCase @Inject constructor() {
    operator fun invoke(
        cart: InMemoryCartRepositoryInterface,
        itemId: Int,
        newQuantity: Int,
        availableQuantity: Int
    ): Boolean = cart.updateItemQuantity(itemId, newQuantity, availableQuantity)
}

/**
 * Получение потока элементов из переданной корзины.
 */
class GetCartItemsUseCase @Inject constructor() {
    operator fun invoke(
        cart: InMemoryCartRepositoryInterface
    ): Flow<List<CartItemDomain>> = cart.getCartItems()
}
