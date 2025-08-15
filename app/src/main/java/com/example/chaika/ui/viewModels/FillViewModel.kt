package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.data.inMemory.InMemoryCartRepositoryInterface
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.TemplateDomain
import com.example.chaika.domain.usecases.AddItemToCartUseCase
import com.example.chaika.domain.usecases.AddOpUseCase
import com.example.chaika.domain.usecases.ApplyTemplateUseCase
import com.example.chaika.domain.usecases.CreateCartUseCase
import com.example.chaika.domain.usecases.GetCartItemsUseCase
import com.example.chaika.domain.usecases.RemoveItemFromCartUseCase
import com.example.chaika.domain.usecases.UpdateQuantityUnlimitedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FillViewModel @Inject constructor(
    createCart: CreateCartUseCase,
    private val getCartItems: GetCartItemsUseCase,
    private val addItemToCart: AddItemToCartUseCase,
    private val removeItemFromCart: RemoveItemFromCartUseCase,
    private val updateQuantityUnlimited: UpdateQuantityUnlimitedUseCase,
    private val applyTemplate: ApplyTemplateUseCase,
    private val addOp: AddOpUseCase
) : ViewModel() {

    /** Собственная in‑memory корзина для сборки пакета */
    private val cart: InMemoryCartRepositoryInterface = createCart()

    /** Поток элементов корзины для UI */
    val items: StateFlow<List<CartItemDomain>> =
        getCartItems(cart)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /** Добавляет товар в корзину */
    fun onAdd(item: CartItemDomain) {
        addItemToCart(cart, item)
    }

    /** Удаляет товар из корзины */
    fun onRemove(itemId: Int) {
        removeItemFromCart(cart, itemId)
    }

    /**
     * Меняет количество товара в корзине без лимита.
     * Может вызываться кнопками "+" / "-" или ручным вводом.
     */
    fun onQuantityChange(itemId: Int, newQuantity: Int) {
        updateQuantityUnlimited(cart, itemId, newQuantity)
    }

    /**
     * Применяет выбранный шаблон:
     * очищает корзину и наполняет товарами из шаблона.
     */
    fun onApplyTemplate(template: TemplateDomain) = viewModelScope.launch {
        applyTemplate(cart, template).also {
            Log.d("FillViewModel", "Cart size after apply: ${cart.getCart().items.size}")
            Log.d("FillViewModel", "Quantity: ${cart.getCart().items.first().quantity}")
        }
    }

    /**
     * Фиксирует операцию ADD:
     * сохраняет содержимое корзины и очищает её.
     */
    fun onAddOperation(conductorId: Int) = viewModelScope.launch {
        addOp(cart, conductorId)
        // после выполнения cart будет очищен внутри use case
    }
}
