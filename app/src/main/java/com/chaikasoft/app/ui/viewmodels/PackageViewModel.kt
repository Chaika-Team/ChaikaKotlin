package com.chaikasoft.app.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaikasoft.app.domain.models.CartItemDomain
import com.chaikasoft.app.domain.models.PackageItemDomain
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var loadProductsJob: Job? = null

    fun loadProducts(cartItems: StateFlow<List<CartItemDomain>>) {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            getPackageItemUseCase()
                .combine(cartItems) { pagingData, cartItemsList ->
                    pagingData.map { productDomain ->
                        productDomain.toPackageProduct(cartItemsList)
                    }
                }
                .onStart {
                    _isLoading.value = true
                }
                .catch { e ->
                    Log.e("PackageViewModel", "Failed to load package products", e)
                    _isLoading.value = false
                }
                .collect { pagingData ->
                    _productsFlow.value = pagingData
                    _isLoading.value = false
                }
        }
    }

    private fun PackageItemDomain.toPackageProduct(cartItems: List<CartItemDomain>): Product {
        val cartItem = cartItems.find {
            it.product.id == productInfoDomain.id
        }
        return if (cartItem != null && cartItem.quantity >= 1) {
            cartItem.toUiModel()
        } else {
            productInfoDomain.toUiModel().copy(quantity = 0)
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
        _isLoading.value = false
        loadProductsJob?.cancel()
        loadProductsJob = null
    }
}
