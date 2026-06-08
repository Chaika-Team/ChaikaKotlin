package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.R
import com.chaikasoft.app.data.inmemory.InMemoryCartRepositoryInterface
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.sealed.AddItemToCartWithLimitResult
import com.chaikasoft.app.domain.sealed.SaveOperationResult
import com.chaikasoft.app.domain.usecases.AddItemToCartWithLimitUseCase
import com.chaikasoft.app.domain.usecases.CreateCartUseCase
import com.chaikasoft.app.domain.usecases.GetAvailableQuantityUseCase
import com.chaikasoft.app.domain.usecases.GetCartItemsUseCase
import com.chaikasoft.app.domain.usecases.RemoveItemFromCartUseCase
import com.chaikasoft.app.domain.usecases.SoldCardOpUseCase
import com.chaikasoft.app.domain.usecases.SoldCashOpUseCase
import com.chaikasoft.app.domain.usecases.UpdateQuantityWithLimitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SaleViewModel @Inject constructor(
    createCart: CreateCartUseCase,
    private val getCartItems: GetCartItemsUseCase,
    private val addItemToCartWithLimitUseCase: AddItemToCartWithLimitUseCase,
    private val removeItemFromCart: RemoveItemFromCartUseCase,
    private val updateQuantityWithLimit: UpdateQuantityWithLimitUseCase,
    private val getAvailableQuantity: GetAvailableQuantityUseCase,
    private val soldCashOp: SoldCashOpUseCase,
    private val soldCardOp: SoldCardOpUseCase
) : ViewModel() {

    /** Собственная in‑memory корзина для продаж */
    private val cart: InMemoryCartRepositoryInterface = createCart()
    private var nextSoldOutNoticeId = 0L
    private var nextStockLimitNoticeId = 0L

    data class SellResultDialog(@StringRes val messageRes: Int)
    data class SoldOutNotice(val id: Long, val productNames: List<String>)
    data class StockLimitNotice(val id: Long, @StringRes val messageRes: Int)

    private val _sellResultDialog = MutableStateFlow<SellResultDialog?>(null)
    val sellResultDialog = _sellResultDialog.asStateFlow()

    private val _soldOutNotice = MutableStateFlow<SoldOutNotice?>(null)
    val soldOutNotice = _soldOutNotice.asStateFlow()

    private val _stockLimitNotice = MutableStateFlow<StockLimitNotice?>(null)
    val stockLimitNotice = _stockLimitNotice.asStateFlow()

    fun dismissSellResultDialog() {
        _sellResultDialog.value = null
    }

    fun dismissSoldOutNotice() {
        _soldOutNotice.value = null
    }

    fun dismissStockLimitNotice() {
        _stockLimitNotice.value = null
    }

    /** Элементы корзины для UI */
    val items: StateFlow<List<CartItemDomain>> =
        getCartItems(cart)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** добавить в корзину */
    fun onAdd(item: CartItemDomain) {
        viewModelScope.launch {
            when (addItemToCartWithLimitUseCase(cart, item)) {
                AddItemToCartWithLimitResult.OutOfStock -> showStockLimitNotice()
                AddItemToCartWithLimitResult.Added,
                AddItemToCartWithLimitResult.AlreadyInCart -> Unit
            }
        }
    }

    /** убрать из корзины */
    fun onRemove(itemId: Int) {
        removeItemFromCart(cart, itemId)
    }

    /** Поменялось количество в диалоге — с учётом остатка */
    fun onQuantityChange(itemId: Int, newQuantity: Int) = viewModelScope.launch {
        val success = updateQuantityWithLimit(cart, itemId, newQuantity)
        if (!success && newQuantity > 0) {
            showStockLimitNotice()
        }
    }

    /** Продать наличными */
    fun onSellCash(conductorId: Int) = viewModelScope.launch {
        sell(conductorId, soldCashOp::invoke, "onSellCash")
    }

    /** Продать по карте */
    fun onSellCard(conductorId: Int) = viewModelScope.launch {
        sell(conductorId, soldCardOp::invoke, "onSellCard")
    }

    private suspend fun sell(
        conductorId: Int,
        sellOperation: suspend (InMemoryCartRepositoryInterface, Int) -> SaveOperationResult,
        logTag: String
    ) {
        try {
            val soldItems = items.value
            when (sellOperation(cart, conductorId)) {
                is SaveOperationResult.Success -> {
                    _sellResultDialog.value = SellResultDialog(
                        messageRes = R.string.sell_success
                    )
                    updateSoldOutNotice(soldItems)
                }

                is SaveOperationResult.EmptyCart ->
                    _sellResultDialog.value = SellResultDialog(R.string.sell_empty_cart)

                is SaveOperationResult.Failure ->
                    _sellResultDialog.value = SellResultDialog(R.string.sell_failure)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("SaleViewModel", "$logTag failed", e)
            _sellResultDialog.value = SellResultDialog(R.string.sell_failure)
        }
    }

    private suspend fun updateSoldOutNotice(soldItems: List<CartItemDomain>) {
        val soldOutProductNames = soldItems.mapNotNull { item ->
            try {
                val availableQuantity = getAvailableQuantity(item.product.id)
                item.product.name.takeIf { availableQuantity <= 0 }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(
                    "SaleViewModel",
                    "Failed to check available quantity for productId=${item.product.id}",
                    e
                )
                null
            }
        }
        if (soldOutProductNames.isNotEmpty()) {
            _soldOutNotice.value = SoldOutNotice(
                id = ++nextSoldOutNoticeId,
                productNames = soldOutProductNames
            )
        }
    }

    private fun showStockLimitNotice() {
        _stockLimitNotice.value = StockLimitNotice(
            id = ++nextStockLimitNoticeId,
            messageRes = R.string.error_out_of_stock
        )
    }
}
