package com.chaikasoft.app.ui.viewModels

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.R
import com.chaikasoft.app.data.inMemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.sealed.SaveOperationResult
import com.chaikasoft.app.domain.usecases.AddItemToCartWithLimitUseCase
import com.chaikasoft.app.domain.usecases.CreateCartUseCase
import com.chaikasoft.app.domain.usecases.GetCartItemsUseCase
import com.chaikasoft.app.domain.usecases.RemoveItemFromCartUseCase
import com.chaikasoft.app.domain.usecases.SoldCardOpUseCase
import com.chaikasoft.app.domain.usecases.SoldCashOpUseCase
import com.chaikasoft.app.domain.usecases.UpdateQuantityWithLimitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    data class SellResultDialog(@StringRes val messageRes: Int)

    private val _sellResultDialog = MutableStateFlow<SellResultDialog?>(null)
    val sellResultDialog = _sellResultDialog.asStateFlow()

    fun dismissSellResultDialog() {
        _sellResultDialog.value = null
    }

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
            // CHK-206
        }
    }

    /** Продать наличными */
    fun onSellCash(conductorId: Int) = viewModelScope.launch {
        try {
            when (soldCashOp(cart, conductorId)) {
                is SaveOperationResult.Success ->
                    _sellResultDialog.value = SellResultDialog(
                        messageRes = R.string.sell_success
                    )
                is SaveOperationResult.EmptyCart ->
                    _sellResultDialog.value = SellResultDialog(R.string.sell_empty_cart)
                is SaveOperationResult.Failure ->
                    _sellResultDialog.value = SellResultDialog(R.string.sell_failure)
            }
        } catch (e: Exception) {
            Log.e("SaleViewModel", "onSellCash failed", e)
            _sellResultDialog.value = SellResultDialog(R.string.sell_failure)
        }
    }

    /** Продать по карте */
    fun onSellCard(conductorId: Int) = viewModelScope.launch {
        try {
            when (soldCardOp(cart, conductorId)) {
                is SaveOperationResult.Success ->
                    _sellResultDialog.value = SellResultDialog(
                        messageRes = R.string.sell_success
                    )
                is SaveOperationResult.EmptyCart ->
                    _sellResultDialog.value = SellResultDialog(R.string.sell_empty_cart)
                is SaveOperationResult.Failure ->
                    _sellResultDialog.value = SellResultDialog(R.string.sell_failure)
            }
        } catch (e: Exception) {
            Log.e("SaleViewModel", "onSellCard failed", e)
            _sellResultDialog.value = SellResultDialog(R.string.sell_failure)
        }
    }
}