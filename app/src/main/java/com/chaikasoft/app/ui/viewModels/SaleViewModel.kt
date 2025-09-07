package com.chaikasoft.app.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.data.inMemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.usecases.AddItemToCartWithLimitUseCase
import com.chaikasoft.app.domain.usecases.GetCartItemsUseCase
import com.chaikasoft.app.domain.usecases.RemoveItemFromCartUseCase
import com.chaikasoft.app.domain.usecases.UpdateQuantityWithLimitUseCase
import com.chaikasoft.app.domain.usecases.CreateCartUseCase
import com.chaikasoft.app.domain.usecases.SoldCashOpUseCase
import com.chaikasoft.app.domain.usecases.SoldCardOpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(
    createCart: CreateCartUseCase,
    private val getCartItems: GetCartItemsUseCase,
    private val addItemToCartWithLimitUseCase: AddItemToCartWithLimitUseCase,
    private val removeItemFromCart: RemoveItemFromCartUseCase,
    private val updateQuantityWithLimit: UpdateQuantityWithLimitUseCase,
    private val soldCashOp: SoldCashOpUseCase,
    private val soldCardOp: SoldCardOpUseCase,
) : ViewModel() {

    /** Собственная in‑memory корзина для продаж */
    private val cart: InMemoryCartRepositoryInterface = createCart()

    /** Элементы корзины для UI */
    val items: StateFlow<List<CartItemDomain>> =
        getCartItems(cart)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** добавить в корзину */
    fun onAdd(item: CartItemDomain) {
        viewModelScope.launch {
            addItemToCartWithLimitUseCase(cart, item)
        }
    }

    /** убрать из корзины */
    fun onRemove(itemId: Int) {
        removeItemFromCart(cart, itemId)
    }

    /** Поменялось количество в диалоге — с учётом остатка */
    fun onQuantityChange(itemId: Int, newQuantity: Int) = viewModelScope.launch {
        val success = updateQuantityWithLimit(cart, itemId, newQuantity)
        if (!success) {
            // TODO: показать ошибку “Больше нет в наличии”
        }
    }

    /** Продать наличными */
    fun onSellCash(conductorId: Int) = viewModelScope.launch {
        soldCashOp(cart, conductorId)
    }

    /** Продать по карте */
    fun onSellCard(conductorId: Int) = viewModelScope.launch {
        soldCardOp(cart, conductorId)
    }
}