package com.example.chaika.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaika.domain.models.CartItemDomain
import com.example.chaika.domain.usecases.GetPackageItemUseCase
import com.example.chaika.ui.dto.Product
import com.example.chaika.ui.mappers.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackageViewModel @Inject constructor(
    private val getPackageItemUseCase: GetPackageItemUseCase,
) : ViewModel() {

    private val _productsFlow = MutableStateFlow<List<Product>>(emptyList())
    val productsFlow: StateFlow<List<Product>> = _productsFlow.asStateFlow()

    private var loadProductsJob: Job? = null

    fun loadProducts(cartItems: StateFlow<List<CartItemDomain>>) {
        loadProductsJob?.cancel()
        loadProductsJob = viewModelScope.launch {
            getPackageItemUseCase()
                .combine(cartItems) { pagingData, cartItemsList ->
                    pagingData.map { productDomain ->
                        val cartItem = cartItemsList.find { it.product.id == productDomain.productInfoDomain.id }
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

    fun clearProductState() {
        _productsFlow.update { emptyList() }
        loadProductsJob?.cancel()
        loadProductsJob = null
    }
}