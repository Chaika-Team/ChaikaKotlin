package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.usecases.GetAvailableQuantityUseCase
import com.chaikasoft.app.domain.usecases.GetPackageItemUseCase
import com.chaikasoft.app.ui.dto.Product
import com.chaikasoft.app.ui.mappers.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PackageViewModel @Inject constructor(
    private val getPackageItemUseCase: GetPackageItemUseCase,
    private val getAvailableQuantityUseCase: GetAvailableQuantityUseCase
) : ViewModel() {

    private val _productsFlow = MutableStateFlow<List<Product>>(emptyList())
    val productsFlow: StateFlow<List<Product>> = _productsFlow.asStateFlow()

    private val _productQuantities = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val productQuantities: StateFlow<Map<Int, Int>> = _productQuantities.asStateFlow()

    private var loadProductsJob: Job? = null

    fun loadProducts(cartItems: StateFlow<List<CartItemDomain>>) {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            getPackageItemUseCase()
                .combine(cartItems) { pagingData, cartItemsList ->
                    pagingData.map { productDomain ->
                        val cartItem = cartItemsList.find {
                            it.product.id ==
                                productDomain.productInfoDomain.id
                        }
                        if (cartItem != null) {
                            // Если товар есть в корзине, проверяем количество
                            if (cartItem.quantity >= 1) {
                                cartItem.toUiModel()
                            } else {
                                // Если количество меньше 1, считаем что товара нет в корзине
                                productDomain.toUiModel().copy(isInCart = false)
                            }
                        } else {
                            productDomain.toUiModel().copy(isInCart = false)
                        }
                    }
                }
                .collect { pagingData ->
                    _productsFlow.value = pagingData
                }
        }
    }

    fun checkProductQuantity(productId: Int, force: Boolean = false) {
        viewModelScope.launch {
            try {
                if (!force && _productQuantities.value.containsKey(productId)) return@launch
                val quantity = getAvailableQuantityUseCase(productId)
                _productQuantities.update { currentMap ->
                    currentMap + (productId to quantity)
                }
            } catch (e: Exception) {
                Log.e(
                    "PackageViewModel",
                    "Failed to get quantity for product with id=$productId",
                    e
                )
            }
        }
    }

    fun refreshAllQuantities() {
        val ids = _productsFlow.value.map { it.id }
        ids.forEach { id ->
            checkProductQuantity(id, force = true)
        }
    }

    fun clearProductState() {
        _productsFlow.update { emptyList() }
        _productQuantities.update { emptyMap() }
        loadProductsJob?.cancel()
        loadProductsJob = null
    }
}
