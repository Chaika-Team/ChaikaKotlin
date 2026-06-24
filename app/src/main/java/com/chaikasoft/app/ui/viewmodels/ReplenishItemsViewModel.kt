package com.chaikasoft.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.PackageItemDomain
import com.chaikasoft.app.domain.usecases.GetPackageItemUseCase
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.dto.ReplenishProductUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

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
    fun getDisplayProducts(
        cartItems: StateFlow<List<CartItemDomain>>
    ): StateFlow<List<ReplenishProductUiModel>> = combine(
        packageItems,
        cartItems
    ) { packageList, cartList ->
        val cartQuantities = cartList.associate { it.product.id to it.quantity }

        packageList.map { packageItem ->
            val cartQuantity = cartQuantities[packageItem.productInfoDomain.id] ?: 0
            val isInCart = cartQuantity > 0

            ReplenishProductUiModel(
                product = Product(
                    id = packageItem.productInfoDomain.id,
                    name = packageItem.productInfoDomain.name,
                    description = packageItem.productInfoDomain.description,
                    image = packageItem.productInfoDomain.image,
                    price = packageItem.productInfoDomain.price,
                    isInCart = isInCart,
                    quantity = cartQuantity
                ),
                packageQuantity = packageItem.currentQuantity
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
