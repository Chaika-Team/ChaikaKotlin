package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.usecases.AddItemToCartUseCase
import com.example.chaika.domain.usecases.CreateCartUseCase
import com.example.chaika.domain.usecases.GetCartItemsUseCase
import com.example.chaika.domain.usecases.RemoveItemFromCartUseCase
import com.example.chaika.domain.usecases.ReplenishUseCase
import com.example.chaika.domain.usecases.UpdateQuantityUnlimitedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReplenishViewModel @Inject constructor(
    createCart: CreateCartUseCase,
    private val getCartItems: GetCartItemsUseCase,
    private val addItemToCart: AddItemToCartUseCase,
    private val removeItemFromCart: RemoveItemFromCartUseCase,
    private val updateQuantityUnlimited: UpdateQuantityUnlimitedUseCase,
    private val replenishOp: ReplenishUseCase
) : ViewModel() {

    /** Собственная in‑memory корзина для операции “Добор” */
    private val cart: InMemoryCartRepositoryInterface = createCart()

    /** Поток элементов корзины для UI */
    val items: StateFlow<List<CartItemDomain>> =
        getCartItems(cart)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Добавляет указанный товар в корзину добора */
    fun onAdd(item: CartItemDomain) {
        addItemToCart(cart, item)
    }

    /** Удаляет товар из корзины добора по его идентификатору */
    fun onRemove(itemId: Int) {
        removeItemFromCart(cart, itemId)
    }

    /**
     * Меняет количество товара в корзине добора без ограничения остатком.
     * Используется при ручном вводе или кнопках "+" / "-".
     */
    fun onQuantityChange(itemId: Int, newQuantity: Int) {
        updateQuantityUnlimited(cart, itemId, newQuantity)
    }

    /**
     * Выполняет операцию “Добор”:
     * сохраняет текущую корзину в БД и очищает её.
     */
    fun onReplenish(conductorId: Int) = viewModelScope.launch {
        replenishOp(cart, conductorId)
        // после этого cart уже очищен внутри use case
    }
}
