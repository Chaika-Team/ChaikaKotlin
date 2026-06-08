package com.chaikasoft.app.domain.usecases

import com.chaikasoft.app.data.inmemory.CartRepositoryFactoryInterface
import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.data.room.repo.RoomCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartDomain
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.CartOperationDomain
import com.chaikasoft.app.domain.models.OperationTypeDomain
import com.chaikasoft.app.domain.sealed.AddItemToCartWithLimitResult
import com.chaikasoft.app.domain.sealed.SaveOperationResult
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Use Case, отвечающий за выдачу новой in‑memory корзины.
 * Инкапсулирует фабрику и скрывает её от ViewModel.
 */
class CreateCartUseCase @Inject constructor(private val factory: CartRepositoryFactoryInterface) {
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
    ): SaveOperationResult {
        return try {
            // 1) Снимок корзины
            val items = cart.getCartItems().first()

            // 2) Инвариант: без позиций ничего не сохраняем
            if (items.isEmpty()) {
                return SaveOperationResult.EmptyCart
            }

            // 3) Сохраняем в БД
            val opId = roomCartRepository.saveCartWithItemsAndOperation(
                CartDomain(items.toMutableList()),
                operation
            )

            // 4) Чистим in-memory корзину
            cart.clearCart()
            SaveOperationResult.Success(opId)
        } catch (t: Throwable) {
            SaveOperationResult.Failure(t.message)
        }
    }
}

/**
 * Operation‑use cases: обёртки над SaveCartWithItemsAndOperationUseCase
 * Каждая устанавливает свой тип операции, и вызывает общий saveUC.
 */
class AddOpUseCase @Inject constructor(private val saveOp: SaveCartWithItemsAndOperationUseCase) {
    suspend operator fun invoke(cart: InMemoryCartRepositoryInterface, conductorId: Int) =
        saveOp(cart, CartOperationDomain(OperationTypeDomain.ADD, conductorId))
}

class SoldCashOpUseCase @Inject constructor(
    private val saveOp: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(cart: InMemoryCartRepositoryInterface, conductorId: Int) =
        saveOp(cart, CartOperationDomain(OperationTypeDomain.SOLD_CASH, conductorId))
}

class SoldCardOpUseCase @Inject constructor(
    private val saveOp: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(cart: InMemoryCartRepositoryInterface, conductorId: Int) =
        saveOp(cart, CartOperationDomain(OperationTypeDomain.SOLD_CART, conductorId))
}

class ReplenishUseCase @Inject constructor(
    private val saveOp: SaveCartWithItemsAndOperationUseCase
) {
    suspend operator fun invoke(cart: InMemoryCartRepositoryInterface, conductorId: Int) =
        saveOp(cart, CartOperationDomain(OperationTypeDomain.REPLENISH, conductorId))
}

/**
 * Добавление товара в корзину.
 */
class AddItemToCartUseCase @Inject constructor() {
    operator fun invoke(cart: InMemoryCartRepositoryInterface, item: CartItemDomain): Boolean =
        cart.addItemToCart(item)
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
    ): AddItemToCartWithLimitResult {
        val available = getAvailableQuantity(item.product.id)
        if (available <= 0) return AddItemToCartWithLimitResult.OutOfStock
        // Репозиторий сам выставляет quantity = 1 и не добавляет дубликаты
        return if (cart.addItemToCart(item)) {
            AddItemToCartWithLimitResult.Added
        } else {
            AddItemToCartWithLimitResult.AlreadyInCart
        }
    }
}

/**
 * Удаление товара из корзины.
 */
class RemoveItemFromCartUseCase @Inject constructor() {
    operator fun invoke(cart: InMemoryCartRepositoryInterface, itemId: Int): Boolean =
        cart.removeItemFromCart(itemId)
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
    operator fun invoke(cart: InMemoryCartRepositoryInterface): Flow<List<CartItemDomain>> =
        cart.getCartItems()
}
