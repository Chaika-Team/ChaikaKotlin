package com.example.chaika.ui.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.models.PackageItemDomain
import com.example.chaika.domain.usecases.GetPackageItemUseCase
import com.example.chaika.ui.dto.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReplenishItemsViewModel @Inject constructor(
    private val getPackageItems: GetPackageItemUseCase
) : ViewModel() {

    /** Поток доступных товаров (пакет проводника) */
    val packageItems: StateFlow<List<PackageItemDomain>> =
        getPackageItems()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Создает поток продуктов для отображения, объединяя товары из пакета с данными корзины.
     * @param cartItems Поток элементов корзины, передаваемый из уровня экрана
     */
    fun getDisplayProducts(cartItems: StateFlow<List<CartItemDomain>>): StateFlow<List<Product>> {
        return combine(
            packageItems,
            cartItems
        ) { packageList, cartList ->
            // Создаем мапу количеств из корзины по ID продукта
            val cartQuantities = cartList.associate { it.product.id to it.quantity }

            // Преобразуем packageItems в Product с учетом количеств из корзины
            packageList.map { packageItem ->
                val cartQuantity = cartQuantities[packageItem.productInfoDomain.id] ?: 0
                val isInCart = cartQuantity > 0

                Product(
                    id = packageItem.productInfoDomain.id,
                    name = packageItem.productInfoDomain.name,
                    description = packageItem.productInfoDomain.description,
                    image = packageItem.productInfoDomain.image,
                    price = packageItem.productInfoDomain.price,
                    isInCart = isInCart,
                    quantity = cartQuantity
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }
}